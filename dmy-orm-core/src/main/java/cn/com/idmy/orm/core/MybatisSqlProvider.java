package cn.com.idmy.orm.core;

import cn.com.idmy.orm.OrmException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.util.Collection;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class MybatisSqlProvider {
    public static final String SUD = "$sud$";
    public static final String SQL_PARAMS = "$sqlParams$";

    public static final String ENTITY = "$entity$";
    public static final String ENTITIES = "$entities$";
    public static final String ENTITY_CLASS = "$$entityClass$";

    public static final String get = "get";
    public static final String find = "find";
    public static final String delete = "delete";
    public static final String update = "update";
    public static final String count = "count";
    public static final String insert = "insert";
    public static final String inserts = "inserts";
    public static final String updateBySql = "updateBySql";

    protected static void clearSelectColumns(Selects<?> select) {
        if (select.hasSelectColumn) {
            select.clearSelectColumns();
            log.warn("select ... from 中间不能有字段或者函数");
        }
    }

    private static String genCommonSql(Map<String, Object> params) {
        var where = (Crud<?, ?>) params.get(SUD);
        putEntityClass(params, where.entityClass());
        var pair = where.sql();
        params.put(SQL_PARAMS, pair.right);
        return pair.left;
    }

    public static void putEntityClass(Map<String, Object> params, Class<?> entityClass) {
        params.put(ENTITY_CLASS, entityClass);
    }

    public static Class<?> getEntityClass(Map<String, Object> params) {
        return (Class<?>) params.get(ENTITY_CLASS);
    }

    public static Collection<Object> findEntities(Map<String, Object> params) {
        return (Collection<Object>) params.get(ENTITIES);
    }

    public String get(Map<String, Object> params) {
        return genCommonSql(params);
    }

    public String find(Map<String, Object> params) {
        return genCommonSql(params);
    }

    public String update(Map<String, Object> params, ProviderContext context) {
        return genCommonSql(params);
    }

    public String delete(Map<String, Object> params) {
        return genCommonSql(params);
    }

    public String count(Map<String, Object> params) {
        var select = (Selects<?>) params.get(SUD);
        clearSelectColumns(select);
        select.limit = null;
        select.offset = null;
        select.select(SqlFn::count);
        putEntityClass(params, select.entityClass());
        var pair = select.sql();
        params.put(SQL_PARAMS, pair.right);
        return pair.left;
    }

    public String insert(Map<String, Object> params) {
        var entity = params.get(ENTITY);
        var entityClass = entity.getClass();
        var generator = new InsertSqlGenerator(entityClass, entity);
        var pair = generator.generate();
        params.put(SQL_PARAMS, pair.right);
        putEntityClass(params, entityClass);
        return pair.left;
    }

    public String inserts(Map<String, Object> params) {
        var entities = findEntities(params);
        if (entities.isEmpty()) {
            throw new OrmException("批量插入的实体集合不能为空");
        }
        var entityClass = entities.iterator().next().getClass();
        var generator = new InsertSqlGenerator(entityClass, entities);
        var pair = generator.generate();
        params.put(SQL_PARAMS, pair.right);
        putEntityClass(params, entityClass);
        return pair.left;
    }

    public String updateBySql(Map<String, Object> params, ProviderContext context) {
        TableInfo table = Tables.getTableByMapperClass(context.getMapperType());
        putEntityClass(params, table.entityClass());
        return (String) params.get(SUD);
    }
}