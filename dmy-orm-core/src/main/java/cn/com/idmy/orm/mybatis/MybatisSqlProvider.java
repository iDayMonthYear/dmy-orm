package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.LambdaWhere;
import cn.com.idmy.orm.core.SqlConsts;
import cn.com.idmy.orm.core.TableManager;
import org.dromara.hutool.core.reflect.FieldUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MybatisSqlProvider {

    private static String buildCommonSql(Map<String, Object> params) {
        var chain = (LambdaWhere<?, ?>) params.get(MybatisConsts.CHAIN);
        var pair = chain.sql();
        params.put(MybatisConsts.SQL_PARAMS, pair.right);
        MybatisConsts.putEntityClass(params, chain.entityClass());
        return pair.left;
    }

    public String get(Map<String, Object> params) {
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

    public String insert(Map<String, Object> params) {
        var entity = params.get(MybatisConsts.ENTITY);
        var entityClass = entity.getClass();
        var tableInfo = TableManager.getTableInfo(entityClass);
        var columns = tableInfo.columns();

        var sql = new StringBuilder(SqlConsts.INSERT_INTO)
                .append(SqlConsts.STRESS_MARK).append(tableInfo.name()).append(SqlConsts.STRESS_MARK)
                .append(SqlConsts.BRACKET_LEFT);

        var values = new StringBuilder(SqlConsts.VALUES).append(SqlConsts.BRACKET_LEFT);

        List<Object> sqlParams = new ArrayList<>(columns.length);
        params.put(MybatisConsts.SQL_PARAMS, sqlParams);

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

        MybatisConsts.putEntityClass(params, entityClass);
        return sql.append(SqlConsts.BRACKET_RIGHT).append(values).append(SqlConsts.BRACKET_RIGHT).toString();
    }

    public String inserts(Map<String, Object> params) {
        var entities = MybatisConsts.findEntities(params);
        if (entities.isEmpty()) {
            throw new OrmException("批量插入的实体集合不能为空");
        }

        var entityClass = entities.iterator().next().getClass();
        var tableInfo = TableManager.getTableInfo(entityClass);
        var columns = tableInfo.columns();

        var sql = new StringBuilder(SqlConsts.INSERT_INTO)
                .append(SqlConsts.STRESS_MARK).append(tableInfo.name()).append(SqlConsts.STRESS_MARK)
                .append(SqlConsts.BRACKET_LEFT);

        // 构建列名部分
        for (int i = 0, size = columns.length; i < size; i++) {
            var column = columns[i];
            sql.append(SqlConsts.STRESS_MARK).append(column.name()).append(SqlConsts.STRESS_MARK);
            if (i < size - 1) {
                sql.append(SqlConsts.DELIMITER);
            }
        }

        sql.append(SqlConsts.BRACKET_RIGHT).append(SqlConsts.VALUES);

        List<Object> sqlParams = new ArrayList<>(columns.length);
        params.put(MybatisConsts.SQL_PARAMS, sqlParams);

        // 构建values部分
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

        MybatisConsts.putEntityClass(params, entityClass);
        return sql.toString();
    }
}