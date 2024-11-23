package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Page;
import cn.com.idmy.base.model.Param;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.Node.Cond;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.collection.CollStreamUtil;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.reflect.FieldUtil;

import java.util.*;

import static cn.com.idmy.base.constant.DefaultConsts.CREATED_AT;
import static cn.com.idmy.base.constant.DefaultConsts.UPDATED_AT;
import static cn.com.idmy.orm.core.MybatisDao.DEFAULT_BATCH_SIZE;

class MybatisDaoDelegate extends MybatisSqlProvider {
    public static <T, ID> int insertOrUpdate(MybatisDao<T, ID> dao, T entity, boolean ignoreNull) {
        ID id = (ID) FieldUtil.getFieldValue(entity, Tables.getIdName(dao));
        if (id == null) {
            return dao.insert(entity);
        } else {
            if (dao.exists(id)) {
                return update(dao, entity, ignoreNull);
            } else {
                return dao.insert(entity);
            }
        }
    }

    public static <T, ID> int update(MybatisDao<T, ID> dao, T entity, boolean ignoreNull) {
        Class<?> entityClass = entity.getClass();
        var id = Tables.getId(entityClass);
        var idValue = FieldUtil.getFieldValue(entity, id.field());
        if (idValue == null) {
            throw new OrmException("主键不能为空");
        }
        var table = Tables.getTableInfo(entityClass);
        var columns = table.columns();
        var sql = new StringBuilder(SqlConsts.UPDATE).append(SqlConsts.STRESS_MARK).append(Tables.getTableName(entityClass)).append(SqlConsts.STRESS_MARK).append(SqlConsts.SET);
        var sqlParams = new ArrayList<>();
        int idx = 0;
        for (int i = 0; i < columns.length; i++) {
            var column = columns[i];
            var value = FieldUtil.getFieldValue(entity, column.field());
            if (!ignoreNull || value != null) {
                idx++;
                sql.append(SqlConsts.STRESS_MARK).append(column.name()).append(SqlConsts.STRESS_MARK).append(SqlConsts.EQUALS_PLACEHOLDER);
                sqlParams.add(hasTypeHandler(column.field(), value));
                if (i < idx - 1) {
                    sql.append(SqlConsts.DELIMITER);
                }
            }
        }
        sql.append(SqlConsts.WHERE).append(SqlConsts.STRESS_MARK).append(id.name()).append(SqlConsts.STRESS_MARK).append(SqlConsts.EQUALS_PLACEHOLDER);
        sqlParams.add(idValue);
        return dao.updateBySql(sql.toString(), sqlParams);
    }

    public static <T, ID> int inserts(MybatisDao<T, ID> dao, Collection<T> entities, int size) {
        if (entities.isEmpty()) {
            throw new OrmException("批量插入的实体集合不能为空");
        }
        if (size <= 0) {
            size = DEFAULT_BATCH_SIZE;
        }
        var entityList = entities instanceof List ? (List<T>) entities : new ArrayList<>(entities);
        int sum = 0;
        int entitiesSize = entities.size();
        int maxIndex = entitiesSize / size + (entitiesSize % size == 0 ? 0 : 1);
        for (int i = 0; i < maxIndex; i++) {
            sum += dao.inserts(entityList.subList(i * size, Math.min(i * size + size, entitiesSize)));
        }
        return sum;
    }

    @Nullable
    public static <T, ID> T get(MybatisDao<T, ID> dao, ID id) {
        var select = Selects.of(dao);
        select.sqlParamsSize(1);
        select.addNode(new Cond(Tables.getIdName(dao), Op.EQ, id));
        return dao.get(select);
    }

    @Nullable
    public static <T, ID, R> R get(MybatisDao<T, ID> dao, ColumnGetter<T, R> col, ID id) {
        var select = Selects.of(dao);
        select.select(col);
        select.sqlParamsSize(1);
        select.addNode(new Cond(Tables.getIdName(dao), Op.EQ, id));
        T t = dao.get(select);
        if (t == null) {
            return null;
        } else {
            return col.get(t);
        }
    }

    @Nullable
    public static <T, ID, R> R get(MybatisDao<T, ID> dao, ColumnGetter<T, R> col, Selects<T> select) {
        clearSelectColumns(select);
        select.limit = 1;
        T t = dao.get(select.select(col));
        if (t == null) {
            return null;
        } else {
            return col.get(t);
        }
    }

    @Nullable
    public static <T, ID> T get(MybatisDao<T, ID> dao, Selects<T> select, @NonNull ColumnGetter<T, ?>[] cols) {
        clearSelectColumns(select);
        select.limit = 1;
        return dao.get(select.select(cols));
    }

    public static <T, ID, R> List<R> find(MybatisDao<T, ID> dao, ColumnGetter<T, R> col, Selects<T> select) {
        clearSelectColumns(select);
        var ts = dao.find(select.select(col));
        return CollStreamUtil.toList(ts, col::get);
    }

    public static <T, ID> List<T> find(MybatisDao<T, ID> dao, Collection<ID> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        } else {
            var select = Selects.of(dao);
            select.sqlParamsSize(1);
            select.addNode(new Cond(Tables.getIdName(dao), Op.IN, ids));
            return dao.find(select);
        }
    }

    public static <T, ID, R> List<R> find(MybatisDao<T, ID> dao, ColumnGetter<T, R> col, Collection<ID> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        } else {
            var select = Selects.of(dao).select(col);
            select.sqlParamsSize(1);
            select.addNode(new Cond(Tables.getIdName(dao), Op.IN, ids));
            return dao.find(select).stream().map(col::get).toList();
        }
    }

    @Nullable
    public static <T, ID, R extends Number> R sqlFn(MybatisDao<T, ID> dao, SqlFnName name, ColumnGetter<T, R> col, Selects<T> select) {
        if (name == SqlFnName.IF_NULL) {
            throw new OrmException("不支持ifnull");
        } else {
            clearSelectColumns(select);
            select.limit = 1;
            T t = dao.get(select.select(() -> new SqlFn<>(name, col)));
            if (t == null) {
                return null;
            } else {
                return col.get(t);
            }
        }
    }

    public static <T, ID> boolean exists(MybatisDao<T, ID> dao, ID id) {
        var select = Selects.of(dao);
        select.sqlParamsSize(1);
        select.addNode(new Cond(Tables.getIdName(dao), Op.EQ, id));
        return dao.count(select) > 0;
    }

    public static <T, ID> int delete(MybatisDao<T, ID> dao, ID id) {
        var delete = Deletes.of(dao);
        delete.sqlParamsSize(1);
        delete.addNode(new Cond(Tables.getIdName(dao), Op.EQ, id));
        return dao.delete(delete);
    }

    public static <T, ID> int delete(MybatisDao<T, ID> dao, Collection<ID> ids) {
        if (ids.isEmpty()) {
            throw new OrmException("批量删除的id集合不能为空");
        } else {
            var delete = Deletes.of(dao);
            delete.sqlParamsSize(1);
            delete.addNode(new Cond(Tables.getIdName(dao), Op.IN, ids));
            return dao.delete(delete);
        }
    }

    public static <T, ID> Map<ID, T> map(MybatisDao<T, ID> dao, @NonNull ID[] ids) {
        if (ids.length == 0) {
            return Collections.emptyMap();
        } else {
            return getMap(dao, ids);
        }
    }

    private static <T, ID> Map<ID, T> getMap(MybatisDao<T, ID> dao, @NonNull Object ids) {
        var select = Selects.of(dao);
        select.sqlParamsSize(1);
        select.addNode(new Cond(Tables.getIdName(dao), Op.IN, ids));
        var entities = dao.find(select);
        return CollStreamUtil.toIdentityMap(entities, Tables::getIdValue);
    }

    public static <T, ID> Map<ID, T> map(MybatisDao<T, ID> dao, @NonNull Collection<ID> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        } else {
            return getMap(dao, ids);
        }
    }

    public static <T, ID, R> Page<T> page(MybatisDao<T, ID> dao, Page<R> pageIn, Selects<T> select) {
        select.limit = pageIn.getPageSize();
        select.offset = pageIn.getOffset();
        select.orderBy(pageIn.getSorts());

        var params = pageIn.getParams();
        if (params instanceof Param<?> param) {
            var entityClass = dao.entityClass();
            var id = param.getId();
            if (id != null) {
                select.addNode(new Cond(Tables.getIdName(entityClass), Op.EQ, id));
            } else {
                var ids = param.getIds();
                if (CollUtil.isNotEmpty(ids)) {
                    select.addNode(new Cond(Tables.getIdName(entityClass), Op.IN, ids));
                } else {
                    var notIds = param.getIds();
                    if (CollUtil.isNotEmpty(ids)) {
                        select.addNode(new Cond(Tables.getIdName(entityClass), Op.NOT_IN, notIds));
                    }
                }
            }
            var createdAts = param.getCreatedAts();
            if (ArrayUtil.isNotEmpty(createdAts) && createdAts.length == 2) {
                String createdAt = Tables.getColumnName(entityClass, CREATED_AT);
                select.addNode(new Cond(createdAt, Op.BETWEEN, createdAts));
            }
            var updatedAts = param.getUpdatedAts();
            if (ArrayUtil.isNotEmpty(updatedAts) && updatedAts.length == 2) {
                String updatedAt = Tables.getColumnName(entityClass, UPDATED_AT);
                select.addNode(new Cond(updatedAt, Op.BETWEEN, createdAts));
            }
        }
        var rows = dao.find(select);
        if (pageIn.getNeedTotal() == null || pageIn.getNeedTotal()) {
            pageIn.setTotal(dao.count(select));
        } else {
            pageIn.setTotal(rows.size());
        }
        return Page.of(pageIn.getPageNo(), pageIn.getPageSize(), pageIn.getTotal(), rows);
    }
}
