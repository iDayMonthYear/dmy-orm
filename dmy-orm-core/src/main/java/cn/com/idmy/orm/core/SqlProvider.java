package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Page;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.SqlNode.SqlCond;
import cn.com.idmy.orm.core.SqlNode.SqlSet;
import cn.com.idmy.orm.mybatis.MybatisUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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

import java.util.*;

import static cn.com.idmy.orm.core.Tables.getIdColumnName;
import static cn.com.idmy.orm.core.Tables.getTableByMapperClass;

@Slf4j
@Accessors(fluent = true)
@RequiredArgsConstructor
public class SqlProvider {
    public static final String CRUD = "$crud$";
    public static final String SQL_PARAMS = "$sqlParams$";
    public static final String ENTITY = "$entity$";
    public static final String ENTITIES = "$entities$";
    public static final String ENTITY_TYPE = "$$entityType$";
    public static final String getNullable0 = "getNullable0";
    public static final String list0 = "list0";
    public static final String delete = "delete";
    public static final String update = "update";
    public static final String count = "count";
    public static final String create = "create";
    public static final String creates = "creates";
    public static final String updateBySql = "updateBySql";
    public static int DEFAULT_BATCH_SIZE = 1000;
    private static SqlSessionFactory sqlSessionFactory;

    public static void sqlSessionFactory(@NotNull SqlSessionFactory factory) {
        sqlSessionFactory = factory;
    }

    protected static void clearSelectColumns(@NotNull Query<?> q) {
        if (q.hasSelectColumn) {
            q.clearSelectColumns();
            log.error("select ... from 中间不能有字段或者函数");
        }
    }

    private static @NotNull String genCommonSql(@NotNull Map<String, Object> params) {
        var where = (Crud<?, ?>) params.get(CRUD);
        putEntityType(params, where.entityType);
        var pair = where.sql();
        params.put(SQL_PARAMS, pair.getRight());
        return pair.getLeft();
    }

    public static void putEntityType(@NotNull Map<String, Object> params, @NotNull Class<?> entityType) {
        params.put(ENTITY_TYPE, entityType);
    }

    public static @NotNull Class<?> getEntityType(@NotNull Map<String, Object> params) {
        return (Class<?>) params.get(ENTITY_TYPE);
    }

    @SuppressWarnings("unchecked")
    public static @NotNull Collection<Object> listEntities(@NotNull Map<String, Object> params) {
        return (Collection<Object>) params.get(ENTITIES);
    }

    public static <T, ID> int update(@NotNull OrmDao<T, ID> dao, @NotNull T entity, boolean ignoreNull) {
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
            if (column.exist()) {
                var field = column.field();
                if (field != idField) {
                    var value = FieldUtil.getFieldValue(entity, field);
                    if (!ignoreNull || value != null) {
                        u.addNode(new SqlSet(column, value));
                    }
                }
            }
        }
        var sql = u.sql();
        return dao.updateBySql(sql.getLeft(), sql.getRight());
    }

    public static <T, ID> int[] update(@NotNull OrmDao<T, ID> dao, @NotNull Collection<T> ls, int size, boolean ignoreNull) {
        var interfaces = ClassUtil.getInterfaces(dao.getClass());
        if (CollUtil.isEmpty(interfaces)) {
            throw new OrmException("dao must be interface");
        } else {
            return MybatisUtil.batch(sqlSessionFactory, ls, size, interfaces.getFirst(), ($, t) -> dao.update(t, ignoreNull));
        }
    }

    public static <T, ID> int creates(@NotNull OrmDao<T, ID> dao, @Nullable Collection<T> ls, int size) {
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

    static @NotNull <T, ID> Map<ID, T> map(@NotNull OrmDao<T, ID> dao, @NonNull Object ids) {
        var q = dao.q();
        q.sqlParamsSize = 1;
        q.addNode(new SqlCond(getIdColumnName(dao), Op.IN, ids));
        return CollStreamUtil.toIdentityMap(dao.list(q), Tables::getIdValue);
    }

    public static @NotNull <T, ID, R> Page<T> page(@NotNull OrmDao<T, ID> dao, @NotNull Page<R> page, @NotNull Query<T> q) {
        q.limit = page.pageSize();
        q.offset = page.offset();
        q.orderBy(page.sorts());
        var hasTotal = page.hasTotal() == null || page.hasTotal();
        if (page.pageSize() == 1 && page.pageNo() == 1) {
            hasTotal = false;
        }

        long total = -1;

        if (hasTotal) {
            var limit = q.limit;
            var offset = q.offset;
            var nodes = q.nodes;
            q.clearSelectColumns();
            total = dao.count(q);
            q.limit = limit;
            q.offset = offset;
            q.nodes = nodes;
        }
        if (total == 0) {
            return Page.empty();
        } else {
            var rows = dao.list(q);
            if (!hasTotal) {
                total = rows.size();
            }
            return Page.of(rows, page.pageNo(), page.pageSize(), total);
        }
    }

    public @NotNull String getNullable0(@NotNull Map<String, Object> params) {
        return genCommonSql(params);
    }

    public @NotNull String list0(@NotNull Map<String, Object> params) {
        return genCommonSql(params);
    }

    public @NotNull String update(@NotNull Map<String, Object> params) {
        return genCommonSql(params);
    }

    public @NotNull String delete(@NotNull Map<String, Object> params) {
        return genCommonSql(params);
    }

    public @NotNull String count(@NotNull Map<String, Object> params) {
        var q = (Query<?>) params.get(CRUD);
        clearSelectColumns(q);
        q.limit = null;
        q.offset = null;
        q.select(SqlFn::count);
        putEntityType(params, q.entityType);
        var pair = q.sql();
        params.put(SQL_PARAMS, pair.getRight());
        return pair.getLeft();
    }

    public @NotNull String create(@NotNull Map<String, Object> params) {
        var entity = params.get(ENTITY);
        var entityType = entity.getClass();
        var generator = new CreateSqlGenerator(entityType, entity);
        var pair = generator.generate();
        params.put(SQL_PARAMS, pair.getRight());
        putEntityType(params, entityType);
        return pair.getLeft();
    }

    public @NotNull String creates(@NotNull Map<String, Object> params) {
        var ls = listEntities(params);
        if (ls.isEmpty()) {
            throw new OrmException("批量创建的实体集合不能为空");
        } else {
            var entityType = ls.iterator().next().getClass();
            var generator = new CreateSqlGenerator(entityType, ls);
            var pair = generator.generate();
            params.put(SQL_PARAMS, pair.getRight());
            putEntityType(params, entityType);
            return pair.getLeft();
        }
    }

    public @NotNull String updateBySql(@NotNull Map<String, Object> params, @NotNull ProviderContext ctx) {
        var table = getTableByMapperClass(ctx.getMapperType());
        putEntityType(params, Objects.requireNonNull(table).entityType());
        return (String) params.get(CRUD);
    }
}