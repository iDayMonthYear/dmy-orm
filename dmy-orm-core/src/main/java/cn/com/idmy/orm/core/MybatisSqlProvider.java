package cn.com.idmy.orm.core;

import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.mybatis.handler.TypeHandlerValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.dromara.hutool.core.reflect.FieldUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
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

    protected static Object hasTypeHandler(Field field, Object value) {
        var handler = Tables.getHandler(field);
        if (handler == null) {
            return value;
        } else {
            return new TypeHandlerValue(handler, value);
        }
    }

    private static String buildCommonSql(Map<String, Object> params) {
        var where = (Sud<?, ?>) params.get(SUD);
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

    public static List<Object> findEntities(Map<String, Object> params) {
        return (List<Object>) params.get(ENTITIES);
    }

    public String get(Map<String, Object> params) {
        return buildCommonSql(params);
    }

    public String find(Map<String, Object> params) {
        return buildCommonSql(params);
    }

    public String update(Map<String, Object> params, ProviderContext context) {
        TableInfo tableInfo = Tables.getTableInfoByMapperClass(context.getMapperType());
        return buildCommonSql(params);
    }

    public String delete(Map<String, Object> params) {
        return buildCommonSql(params);
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

    private StringBuilder builderInsertHeader(String name) {
        return new StringBuilder(SqlConsts.INSERT_INTO)
                .append(SqlConsts.STRESS_MARK)
                .append(name)
                .append(SqlConsts.STRESS_MARK)
                .append(SqlConsts.BLANK)
                .append(SqlConsts.BRACKET_LEFT);
    }

    public String insert(Map<String, Object> params) {
        var entity = params.get(ENTITY);
        var table = Tables.getTableInfo(entity.getClass());
        var columns = table.columns();

        var sql = builderInsertHeader(table.name());

        var values = new StringBuilder(SqlConsts.VALUES).append(SqlConsts.BRACKET_LEFT);

        var sqlParams = new ArrayList<>(columns.length);

        for (int i = 0, size = columns.length; i < size; i++) {
            var column = columns[i];
            sql.append(SqlConsts.STRESS_MARK).append(column.name()).append(SqlConsts.STRESS_MARK);
            values.append(SqlConsts.PLACEHOLDER);

            var value = FieldUtil.getFieldValue(entity, column.field());
            sqlParams.add(hasTypeHandler(column.field(), value));

            if (i < size - 1) {
                sql.append(SqlConsts.DELIMITER);
                values.append(SqlConsts.DELIMITER);
            }
        }

        params.put(SQL_PARAMS, sqlParams);
        putEntityClass(params, entity.getClass());
        return sql.append(SqlConsts.BRACKET_RIGHT).append(values).append(SqlConsts.BRACKET_RIGHT).toString();
    }

    public String inserts(Map<String, Object> params) {
        var entities = findEntities(params);
        if (entities.isEmpty()) {
            throw new OrmException("批量插入的实体集合不能为空");
        }
        var table = Tables.getTableInfo(entities.getFirst().getClass());
        var columns = table.columns();
        var sql = builderInsertHeader(table.name());

        // 构建列名部分
        for (int i = 0, size = columns.length; i < size; i++) {
            var column = columns[i];
            sql.append(SqlConsts.STRESS_MARK).append(column.name()).append(SqlConsts.STRESS_MARK);
            if (i < size - 1) {
                sql.append(SqlConsts.DELIMITER);
            }
        }
        sql.append(SqlConsts.BRACKET_RIGHT);

        // 构建values部分
        sql.append(SqlConsts.VALUES);
        var sqlParams = new ArrayList<>(columns.length);
        var first = true;
        for (var entity : entities) {
            if (first) {
                first = false;
            } else {
                sql.append(SqlConsts.DELIMITER);
            }
            sql.append(SqlConsts.BRACKET_LEFT);
            for (int i = 0, size = columns.length; i < size; i++) {
                sql.append(SqlConsts.PLACEHOLDER);
                Object value = FieldUtil.getFieldValue(entity, columns[i].name());
                sqlParams.add(hasTypeHandler(columns[i].field(), value));
                if (i < size - 1) {
                    sql.append(SqlConsts.DELIMITER);
                }
            }
            sql.append(SqlConsts.BRACKET_RIGHT);
        }
        params.put(SQL_PARAMS, sqlParams);
        putEntityClass(params, entities.getFirst().getClass());
        return sql.toString();
    }

    public String updateBySql(Map<String, Object> params, ProviderContext context) {
        TableInfo tableInfo = Tables.getTableInfoByMapperClass(context.getMapperType());
        putEntityClass(params, tableInfo.entityClass());
        return (String) params.get(SUD);
    }
}