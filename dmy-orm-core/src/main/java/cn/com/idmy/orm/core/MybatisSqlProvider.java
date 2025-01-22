package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Page;
import cn.com.idmy.base.model.Param;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.SqlNode.SqlCond;
import cn.com.idmy.orm.core.SqlNode.SqlSet;
import cn.com.idmy.orm.mybatis.MybatisUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.session.SqlSessionFactory;
import org.dromara.hutool.core.collection.CollStreamUtil;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.reflect.ClassUtil;
import org.dromara.hutool.core.reflect.FieldUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.com.idmy.orm.core.Tables.getIdName;
import static cn.com.idmy.orm.core.Tables.getTableByMapperClass;

@Slf4j
@Accessors(fluent = true)
@RequiredArgsConstructor
public class MybatisSqlProvider {
    @Setter
    private static SqlSessionFactory sqlSessionFactory;

    public static int DEFAULT_BATCH_SIZE = 1000;
    public static final String CRUD = "$crud$";
    public static final String SQL_PARAMS = "$sqlParams$";

    public static final String ENTITY = "$entity$";
    public static final String ENTITIES = "$entities$";
    public static final String ENTITY_TYPE = "$$entityType$";

    public static final String getNullable = "getNullable";
    public static final String list0 = "list0";
    public static final String delete = "delete";
    public static final String update = "update";
    public static final String count = "count";
    public static final String create = "create";
    public static final String creates = "creates";
    public static final String updateBySql = "updateBySql";

    protected static void clearSelectColumns(@NotNull Query<?> q) {
        if (q.hasSelectColumn) {
            q.clearSelectColumns();
            log.error("select ... from 中间不能有字段或者函数");
        }
    }

    @NotNull
    private static String genCommonSql(@NotNull Map<String, Object> params) {
        var where = (Crud<?, ?>) params.get(CRUD);
        putEntityType(params, where.entityType);
        var pair = where.sql();
        params.put(SQL_PARAMS, pair.right);
        return pair.left;
    }

    public static void putEntityType(@NotNull Map<String, Object> params, @NotNull Class<?> entityType) {
        params.put(ENTITY_TYPE, entityType);
    }

    @NotNull
    public static Class<?> getEntityType(@NotNull Map<String, Object> params) {
        return (Class<?>) params.get(ENTITY_TYPE);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public static Collection<Object> listEntity(@NotNull Map<String, Object> params) {
        return (Collection<Object>) params.get(ENTITIES);
    }

    public static <T, ID> int update(@NotNull MybatisDao<T, ID> dao, @NotNull T entity, boolean ignoreNull) {
        var table = Tables.getTable(entity.getClass());
        var id = table.id();
        var idField = id.field();
        var idValue = FieldUtil.getFieldValue(entity, idField);
        if (idValue == null) {
            throw new OrmException("主键不能为空");
        }
        var u = dao.u().addNode(new SqlCond(id.name(), Op.EQ, idValue));
        var cols = table.columns();
        int size = cols.length;
        for (int i = 0; i < size; i++) {
            var column = cols[i];
            var field = column.field();
            if (field != idField) {
                var value = FieldUtil.getFieldValue(entity, field);
                if (!ignoreNull || value != null) {
                    u.addNode(new SqlSet(column, value));
                }
            }
        }
        var sql = u.sql();
        return dao.updateBySql(sql.left, sql.right);
    }

    public static <T, ID> int[] update(@NotNull MybatisDao<T, ID> dao, @NotNull Collection<T> ls, int size, boolean ignoreNull) {
        var interfaces = ClassUtil.getInterfaces(dao.getClass());
        if (CollUtil.isEmpty(interfaces)) {
            throw new OrmException("dao must be interface");
        } else {
            return MybatisUtil.batch(sqlSessionFactory, ls, size, interfaces.getFirst(), ($, t) -> dao.update(t, ignoreNull));
        }
    }

    public static <T, ID> int creates(@NotNull MybatisDao<T, ID> dao, @Nullable Collection<T> ls, int size) {
        if (CollUtil.isEmpty(ls)) {
            return -1;
        } else {
            if (size <= 0) {
                size = DEFAULT_BATCH_SIZE;
            }
            var entityList = ls instanceof List ? (List<T>) ls : new ArrayList<>(ls);
            int sum = 0;
            int entitiesSize = ls.size();
            int maxIdx = entitiesSize / size + (entitiesSize % size == 0 ? 0 : 1);
            for (int i = 0; i < maxIdx; i++) {
                sum += dao.creates(entityList.subList(i * size, Math.min(i * size + size, entitiesSize)));
            }
            return sum;
        }
    }

    @NotNull
    static <T, ID> Map<ID, T> map(@NotNull MybatisDao<T, ID> dao, @NonNull Object ids) {
        var q = dao.q();
        q.sqlParamsSize = 1;
        q.addNode(new SqlCond(getIdName(dao), Op.IN, ids));
        return CollStreamUtil.toIdentityMap(dao.list(q), Tables::getIdValue);
    }

    @NotNull
    public static <T, ID, R> Page<T> page(@NotNull MybatisDao<T, ID> dao, @NotNull Page<R> page, @NotNull Query<T> q) {
        q.limit = page.pageSize();
        q.offset = page.offset();
        q.orderBy(page.sorts());

        var params = page.params();
        if (params instanceof Param<?> param) {
            q.param(param);
        }
        var rows = dao.list(q);
        if (page.hasTotal() == null || page.hasTotal()) {
            page.total(dao.count(q));
        } else {
            page.total(rows.size());
        }
        return Page.of(page.pageNo(), page.pageSize(), page.total(), rows);
    }

    @NotNull
    public String getNullable(@NotNull Map<String, Object> params) {
        return genCommonSql(params);
    }

    @NotNull
    public String list0(@NotNull Map<String, Object> params) {
        return genCommonSql(params);
    }

    @NotNull
    public String update(@NotNull Map<String, Object> params) {
        return genCommonSql(params);
    }

    @NotNull
    public String delete(@NotNull Map<String, Object> params) {
        return genCommonSql(params);
    }

    @NotNull
    public String count(@NotNull Map<String, Object> params) {
        var q = (Query<?>) params.get(CRUD);
        clearSelectColumns(q);
        q.limit = null;
        q.offset = null;
        q.select(SqlFn::count);
        putEntityType(params, q.entityType);
        var pair = q.sql();
        params.put(SQL_PARAMS, pair.right);
        return pair.left;
    }

    @NotNull
    public String create(@NotNull Map<String, Object> params) {
        var entity = params.get(ENTITY);
        var entityType = entity.getClass();
        var generator = new CreateSqlGenerator(entityType, entity);
        var pair = generator.gen();
        params.put(SQL_PARAMS, pair.right);
        putEntityType(params, entityType);
        return pair.left;
    }

    @NotNull
    public String creates(@NotNull Map<String, Object> params) {
        var ls = listEntity(params);
        if (ls.isEmpty()) {
            throw new OrmException("批量创建的实体集合不能为空");
        } else {
            var entityType = ls.iterator().next().getClass();
            var generator = new CreateSqlGenerator(entityType, ls);
            var pair = generator.gen();
            params.put(SQL_PARAMS, pair.right);
            putEntityType(params, entityType);
            return pair.left;
        }
    }

    @NotNull
    public String updateBySql(@NotNull Map<String, Object> params, @NotNull ProviderContext context) {
        var table = getTableByMapperClass(context.getMapperType());
        putEntityType(params, table.entityType());
        return (String) params.get(CRUD);
    }
}