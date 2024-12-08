package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Page;
import cn.com.idmy.base.model.Param;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.SqlNode.SqlCond;
import cn.com.idmy.orm.core.SqlNode.SqlSet;
import jakarta.annotation.Nullable;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.collection.CollStreamUtil;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.reflect.FieldUtil;

import java.util.*;

import static cn.com.idmy.base.constant.DefaultConsts.CREATED_AT;
import static cn.com.idmy.base.constant.DefaultConsts.UPDATED_AT;
import static cn.com.idmy.orm.core.MybatisDao.DEFAULT_BATCH_SIZE;
import static cn.com.idmy.orm.core.MybatisSqlProvider.clearSelectColumns;
import static cn.com.idmy.orm.core.Tables.getColumnName;
import static cn.com.idmy.orm.core.Tables.getIdName;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
class MybatisDaoDelegate {
    public static <T, ID> int insertOrUpdate(MybatisDao<T, ID> dao, T entity, boolean ignoreNull) {
        var idField = Tables.getIdField(entity.getClass());
        var idVal = FieldUtil.getFieldValue(entity, idField);
        if (idVal == null) {
            return dao.insert(entity);
        } else {
            if (dao.exists((ID) idVal)) {
                return update(dao, entity, ignoreNull);
            } else {
                return dao.insert(entity);
            }
        }
    }

    public static <T, ID> int update(MybatisDao<T, ID> dao, T entity, boolean ignoreNull) {
        var table = Tables.getTable(entity.getClass());
        var id = table.id();
        var idField = id.field();
        var idValue = FieldUtil.getFieldValue(entity, idField);
        if (idValue == null) {
            throw new OrmException("主键不能为空");
        }
        var updates = Updates.of(dao).addNode(new SqlCond(id.name(), Op.EQ, idValue));
        var columns = table.columns();
        int size = columns.length;
        for (int i = 0; i < size; i++) {
            var column = columns[i];
            var field = column.field();
            if (field != idField) {
                var value = FieldUtil.getFieldValue(entity, field);
                if (!ignoreNull || value != null) {
                    updates.addNode(new SqlSet(column.name(), value));
                }
            }
        }
        var sql = updates.sql();
        return dao.updateBySql(sql.left, sql.right);
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
        int maxIdx = entitiesSize / size + (entitiesSize % size == 0 ? 0 : 1);
        for (int i = 0; i < maxIdx; i++) {
            sum += dao.inserts(entityList.subList(i * size, Math.min(i * size + size, entitiesSize)));
        }
        return sum;
    }

    @Nullable
    public static <T, ID> T get(MybatisDao<T, ID> dao, ID id) {
        var crud = Selects.of(dao);
        crud.sqlParamsSize = 1;
        crud.addNode(new SqlCond(getIdName(dao), Op.EQ, id));
        return dao.get(crud);
    }

    @Nullable
    public static <T, ID, R> R get(MybatisDao<T, ID> dao, FieldGetter<T, R> field, ID id) {
        var crud = Selects.of(dao);
        crud.select(field);
        crud.sqlParamsSize = 1;
        crud.addNode(new SqlCond(getIdName(dao), Op.EQ, id));
        T t = dao.get(crud);
        return t == null ? null : field.get(t);
    }

    @Nullable
    public static <T, ID, R> R get(MybatisDao<T, ID> dao, FieldGetter<T, R> field, Selects<T> crud) {
        clearSelectColumns(crud);
        crud.limit = 1;
        T t = dao.get(crud.select(field));
        return t == null ? null : field.get(t);
    }

    @Nullable
    public static <T, ID> T get(MybatisDao<T, ID> dao, Selects<T> crud, @NonNull FieldGetter<T, ?>[] cols) {
        clearSelectColumns(crud);
        crud.limit = 1;
        return dao.get(crud.select(cols));
    }

    public static <T, ID, R> List<R> find(MybatisDao<T, ID> dao, FieldGetter<T, R> field, Selects<T> crud) {
        clearSelectColumns(crud);
        var ts = dao.find(crud.select(field));
        return CollStreamUtil.toList(ts, field::get);
    }

    public static <T, ID> List<T> find(MybatisDao<T, ID> dao, Collection<ID> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        } else {
            var crud = Selects.of(dao);
            crud.sqlParamsSize = 1;
            crud.addNode(new SqlCond(getIdName(dao), Op.IN, ids));
            return dao.find(crud);
        }
    }

    public static <T, ID, R> List<R> find(MybatisDao<T, ID> dao, FieldGetter<T, R> field, Collection<ID> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        } else {
            var crud = Selects.of(dao).select(field);
            crud.sqlParamsSize = 1;
            crud.addNode(new SqlCond(getIdName(dao), Op.IN, ids));
            return dao.find(crud).stream().map(field::get).toList();
        }
    }

    @Nullable
    public static <T, ID, R extends Number> R sqlFn(MybatisDao<T, ID> dao, SqlFnName name, FieldGetter<T, R> field, Selects<T> crud) {
        if (name == SqlFnName.IF_NULL) {
            throw new OrmException("不支持ifnull");
        } else {
            clearSelectColumns(crud);
            crud.limit = 1;
            T t = dao.get(crud.select(() -> new SqlFn<>(name, field)));
            return t == null ? null : field.get(t);
        }
    }

    public static <T, ID> boolean exists(MybatisDao<T, ID> dao, ID id) {
        var crud = Selects.of(dao);
        crud.sqlParamsSize = 1;
        crud.addNode(new SqlCond(getIdName(dao), Op.EQ, id));
        return dao.count(crud) > 0;
    }

    public static <T, ID> int delete(MybatisDao<T, ID> dao, ID id) {
        var crud = Deletes.of(dao);
        crud.sqlParamsSize = 1;
        crud.addNode(new SqlCond(getIdName(dao), Op.EQ, id));
        return dao.delete(crud);
    }

    public static <T, ID> int delete(MybatisDao<T, ID> dao, Collection<ID> ids) {
        if (ids.isEmpty()) {
            throw new OrmException("批量删除的id集合不能为空");
        } else {
            var crud = Deletes.of(dao);
            crud.sqlParamsSize = 1;
            crud.addNode(new SqlCond(getIdName(dao), Op.IN, ids));
            return dao.delete(crud);
        }
    }

    public static <T, ID> Map<ID, T> map(MybatisDao<T, ID> dao, @NonNull ID[] ids) {
        return ids.length == 0 ? Collections.emptyMap() : getMap(dao, ids);
    }

    private static <T, ID> Map<ID, T> getMap(MybatisDao<T, ID> dao, @NonNull Object ids) {
        var crud = Selects.of(dao);
        crud.sqlParamsSize = 1;
        crud.addNode(new SqlCond(getIdName(dao), Op.IN, ids));
        var entities = dao.find(crud);
        return CollStreamUtil.toIdentityMap(entities, Tables::getIdValue);
    }

    public static <T, ID> Map<ID, T> map(MybatisDao<T, ID> dao, @NonNull Collection<ID> ids) {
        return ids.isEmpty() ? Collections.emptyMap() : getMap(dao, ids);
    }

    public static <T, ID, R> Page<T> page(MybatisDao<T, ID> dao, Page<R> page, Selects<T> crud) {
        crud.limit = page.getPageSize();
        crud.offset = page.getOffset();
        crud.orderBy(page.getSorts());

        var params = page.getParams();
        if (params instanceof Param<?> param) {
            var entityClass = dao.entityClass();
            var id = param.getId();
            if (id != null) {
                crud.addNode(new SqlCond(getIdName(entityClass), Op.EQ, id));
            } else {
                var ids = param.getIds();
                if (CollUtil.isNotEmpty(ids)) {
                    crud.addNode(new SqlCond(getIdName(entityClass), Op.IN, ids));
                } else {
                    var notIds = param.getIds();
                    if (CollUtil.isNotEmpty(notIds)) {
                        crud.addNode(new SqlCond(getIdName(entityClass), Op.NOT_IN, notIds));
                    }
                }
            }
            var createdAts = param.getCreatedAts();
            if (ArrayUtil.isNotEmpty(createdAts) && createdAts.length == 2) {
                var createdAt = getColumnName(entityClass, CREATED_AT);
                if (createdAt != null) {
                    crud.addNode(new SqlCond(createdAt, Op.BETWEEN, createdAts));
                }
            }
            var updatedAts = param.getUpdatedAts();
            if (ArrayUtil.isNotEmpty(updatedAts) && updatedAts.length == 2) {
                var updatedAt = getColumnName(entityClass, UPDATED_AT);
                if (updatedAt != null) {
                    crud.addNode(new SqlCond(updatedAt, Op.BETWEEN, createdAts));
                }
            }
        }
        var rows = dao.find(crud);
        if (page.getNeedTotal() == null || page.getNeedTotal()) {
            page.setTotal(dao.count(crud));
        } else {
            page.setTotal(rows.size());
        }
        return Page.of(page.getPageNo(), page.getPageSize(), page.getTotal(), rows);
    }
}
