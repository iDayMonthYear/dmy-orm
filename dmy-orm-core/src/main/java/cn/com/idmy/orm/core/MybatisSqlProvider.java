package cn.com.idmy.orm.core;

import cn.com.idmy.orm.OrmException;
import org.dromara.hutool.core.reflect.FieldUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MybatisSqlProvider {
    public static final String CHAIN = "$chain$";

    public static final String SQL_PARAMS = "$sqlParams$";

    public static final String ENTITY = "$entity$";
    public static final String ENTITIES = "$entities$";

    public static final String GET = "get";
    public static final String FIND = "find";
    public static final String DELETE = "delete";
    public static final String UPDATE = "update";
    public static final String COUNT = "count";
    public static final String INSERT = "insert";
    public static final String INSERTS = "inserts";

    private static final String ENTITY_CLASS = "$$entityClass$";

    public static void putEntityClass(Map<String, Object> params, Class<?> entityClass) {
        params.put(ENTITY_CLASS, entityClass);
    }

    public static Class<?> getEntityClass(Map<String, Object> params) {
        return (Class<?>) params.get(ENTITY_CLASS);
    }

    public static List<Object> findEntities(Map<String, Object> params) {
        return (List<Object>) params.get(ENTITIES);
    }

    private static String buildCommonSql(Map<String, Object> params) {
        var where = (AbstractWhere<?, ?>) params.get(CHAIN);
        putEntityClass(params, where.entityClass());
        var pair = where.sql();
        params.put(SQL_PARAMS, pair.right);
        return pair.left;
    }


    public String get(Map<String, Object> params) {
        var where = (SelectChain<?>) params.get(CHAIN);
        if (!where.onlyOne) {
            where.limit(1);
        }
        return buildCommonSql(params);
    }

    public String find(Map<String, Object> params) {
        return buildCommonSql(params);
    }

    public String update(Map<String, Object> params) {
        return buildCommonSql(params);
    }

    public String delete(Map<String, Object> params) {
        return buildCommonSql(params);
    }

    public String count(Map<String, Object> params) {
        var where = (SelectChain<?>) params.get(CHAIN);
        if (where.hasSelectColumn) {
            throw new IllegalArgumentException("select ... from 中间不能有字段或者函数");
        }
        where.select(SqlFn::count);
        putEntityClass(params, where.entityClass());
        var pair = where.sql();
        params.put(SQL_PARAMS, pair.right);
        return pair.left;
    }

    public String insert(Map<String, Object> params) {
        var entity = params.get(ENTITY);
        var table = TableManager.getTableInfo(entity.getClass());
        var columns = table.columns();

        var sql = builderInsertHeader(table.name());

        var values = new StringBuilder(SqlConsts.VALUES).append(SqlConsts.BRACKET_LEFT);

        var sqlParams = new ArrayList<>(columns.length);

        for (int i = 0, size = columns.length; i < size; i++) {
            var column = columns[i];
            sql.append(SqlConsts.STRESS_MARK).append(column.name()).append(SqlConsts.STRESS_MARK);
            values.append(SqlConsts.PLACEHOLDER);

            sqlParams.add(FieldUtil.getFieldValue(entity, column.field()));

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
        var table = TableManager.getTableInfo(entities.getFirst().getClass());
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
                sqlParams.add(FieldUtil.getFieldValue(entity, columns[i].name()));
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

    private static StringBuilder builderInsertHeader(String name) {
        return new StringBuilder(SqlConsts.INSERT_INTO)
                .append(SqlConsts.STRESS_MARK)
                .append(name)
                .append(SqlConsts.STRESS_MARK)
                .append(SqlConsts.BLANK)
                .append(SqlConsts.BRACKET_LEFT);
    }

}