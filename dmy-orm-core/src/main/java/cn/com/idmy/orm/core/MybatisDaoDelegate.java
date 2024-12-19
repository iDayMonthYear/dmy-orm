package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Page;
import cn.com.idmy.base.model.Param;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.SqlNode.SqlCond;
import cn.com.idmy.orm.core.SqlNode.SqlSet;
import jakarta.annotation.Nullable;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.dromara.hutool.core.collection.CollStreamUtil;
import org.dromara.hutool.core.reflect.FieldUtil;

import java.util.*;

import static cn.com.idmy.orm.core.MybatisDao.DEFAULT_BATCH_SIZE;
import static cn.com.idmy.orm.core.MybatisSqlProvider.clearSelectColumns;
import static cn.com.idmy.orm.core.Tables.getIdName;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
class MybatisDaoDelegate {
    public static <T, ID> int createOrUpdate(MybatisDao<T, ID> dao, T entity, boolean ignoreNull) {
        ID idVal = Tables.getIdValue(entity);
        if (idVal == null) {
            return dao.create(entity);
        } else {
            if (dao.exists(idVal)) {
                return update(dao, entity, ignoreNull);
            } else {
                return dao.create(entity);
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
        var updates = Update.of(dao).addNode(new SqlCond(id.name(), Op.EQ, idValue));
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

    public static <T, ID> int creates(MybatisDao<T, ID> dao, Collection<T> entities, int size) {
        if (entities.isEmpty()) {
            throw new OrmException("批量创建的实体集合不能为空");
        }
        if (size <= 0) {
            size = DEFAULT_BATCH_SIZE;
        }
        var entityList = entities instanceof List ? (List<T>) entities : new ArrayList<>(entities);
        int sum = 0;
        int entitiesSize = entities.size();
        int maxIdx = entitiesSize / size + (entitiesSize % size == 0 ? 0 : 1);
        for (int i = 0; i < maxIdx; i++) {
            sum += dao.creates(entityList.subList(i * size, Math.min(i * size + size, entitiesSize)));
        }
        return sum;
    }

    @Nullable
    public static <T, ID> T get(MybatisDao<T, ID> dao, ID id) {
        var query = Query.of(dao);
        query.sqlParamsSize = 1;
        query.addNode(new SqlCond(getIdName(dao), Op.EQ, id));
        return dao.get(query);
    }

    @Nullable
    public static <T, ID, R> R get(MybatisDao<T, ID> dao, FieldGetter<T, R> field, ID id) {
        var query = Query.of(dao);
        query.select(field);
        query.sqlParamsSize = 1;
        query.addNode(new SqlCond(getIdName(dao), Op.EQ, id));
        T t = dao.get(query);
        return t == null ? null : field.get(t);
    }

    @Nullable
    public static <T, ID, R> R get(MybatisDao<T, ID> dao, FieldGetter<T, R> field, Query<T> query) {
        clearSelectColumns(query);
        query.limit = 1;
        T t = dao.get(query.select(field));
        return t == null ? null : field.get(t);
    }

    @Nullable
    public static <T, ID> T get(MybatisDao<T, ID> dao, Query<T> query, @NonNull FieldGetter<T, ?>[] cols) {
        clearSelectColumns(query);
        query.limit = 1;
        return dao.get(query.select(cols));
    }

    public static <T, ID, R> List<R> find(MybatisDao<T, ID> dao, FieldGetter<T, R> field, Query<T> query) {
        clearSelectColumns(query);
        var ts = dao.find(query.select(field));
        return CollStreamUtil.toList(ts, field::get);
    }

    public static <T, ID> List<T> find(MybatisDao<T, ID> dao, Collection<ID> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        } else {
            var query = Query.of(dao);
            query.sqlParamsSize = 1;
            query.addNode(new SqlCond(getIdName(dao), Op.IN, ids));
            return dao.find(query);
        }
    }

    public static <T, ID, R> List<R> find(MybatisDao<T, ID> dao, FieldGetter<T, R> field, Collection<ID> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        } else {
            var select = Query.of(dao).select(field);
            select.sqlParamsSize = 1;
            select.addNode(new SqlCond(getIdName(dao), Op.IN, ids));
            return dao.find(select).stream().map(field::get).toList();
        }
    }

    @Nullable
    public static <T, ID, R extends Number> R sqlFn(MybatisDao<T, ID> dao, SqlFnName name, FieldGetter<T, R> field, Query<T> query) {
        if (name == SqlFnName.IF_NULL) {
            throw new OrmException("不支持ifnull");
        } else {
            clearSelectColumns(query);
            query.limit = 1;
            T t = dao.get(query.select(() -> new SqlFn<>(name, field)));
            return t == null ? null : field.get(t);
        }
    }

    public static <T, ID> boolean exists(MybatisDao<T, ID> dao, ID id) {
        var query = Query.of(dao);
        query.sqlParamsSize = 1;
        query.addNode(new SqlCond(getIdName(dao), Op.EQ, id));
        return dao.count(query) > 0;
    }

    public static <T, ID> int delete(MybatisDao<T, ID> dao, ID id) {
        var delete = Delete.of(dao);
        delete.sqlParamsSize = 1;
        delete.addNode(new SqlCond(getIdName(dao), Op.EQ, id));
        return dao.delete(delete);
    }

    public static <T, ID> int delete(MybatisDao<T, ID> dao, Collection<ID> ids) {
        if (ids.isEmpty()) {
            throw new OrmException("批量删除的id集合不能为空");
        } else {
            var delete = Delete.of(dao);
            delete.sqlParamsSize = 1;
            delete.addNode(new SqlCond(getIdName(dao), Op.IN, ids));
            return dao.delete(delete);
        }
    }

    public static <T, ID> Map<ID, T> map(MybatisDao<T, ID> dao, @NonNull ID[] ids) {
        return ids.length == 0 ? Collections.emptyMap() : getMap(dao, ids);
    }

    private static <T, ID> Map<ID, T> getMap(MybatisDao<T, ID> dao, @NonNull Object ids) {
        var query = Query.of(dao);
        query.sqlParamsSize = 1;
        query.addNode(new SqlCond(getIdName(dao), Op.IN, ids));
        var entities = dao.find(query);
        return CollStreamUtil.toIdentityMap(entities, Tables::getIdValue);
    }

    public static <T, ID> Map<ID, T> map(MybatisDao<T, ID> dao, @NonNull Collection<ID> ids) {
        return ids.isEmpty() ? Collections.emptyMap() : getMap(dao, ids);
    }

    public static <T, ID, R> Page<T> page(MybatisDao<T, ID> dao, Page<R> page, Query<T> query) {
        query.limit = page.getPageSize();
        query.offset = page.getOffset();
        query.orderBy(page.getSorts());

        var params = page.getParams();
        if (params instanceof Param<?> param) {
            query.param(param);
        }
        var rows = dao.find(query);
        if (page.getNeedTotal() == null || page.getNeedTotal()) {
            page.setTotal(dao.count(query));
        } else {
            page.setTotal(rows.size());
        }
        return Page.of(page.getPageNo(), page.getPageSize(), page.getTotal(), rows);
    }
}
