package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.*;
import org.dromara.hutool.core.reflect.FieldUtil;

import java.util.ArrayList;
import java.util.Map;

public class MybatisSqlProvider {
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

    public String count(Map<String, Object> params) {
        var where = (SelectChain<?>) params.get(MybatisConsts.CHAIN);
        if (where.hasSelectColumn()) {
            throw new IllegalArgumentException("select ... from 中间不能有字段或者函数");
        }
        where.select(SqlFn::count);
        MybatisConsts.putEntityClass(params, where.entityClass());
        var pair = where.sql();
        params.put(MybatisConsts.SQL_PARAMS, pair.right);
        return pair.left;
    }

    public String insert(Map<String, Object> params) {
        var entity = params.get(MybatisConsts.ENTITY);
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

        params.put(MybatisConsts.SQL_PARAMS, sqlParams);
        MybatisConsts.putEntityClass(params, entity.getClass());
        return sql.append(SqlConsts.BRACKET_RIGHT).append(values).append(SqlConsts.BRACKET_RIGHT).toString();
    }

    public String inserts(Map<String, Object> params) {
        var entities = MybatisConsts.findEntities(params);
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
        params.put(MybatisConsts.SQL_PARAMS, sqlParams);
        MybatisConsts.putEntityClass(params, entities.getFirst().getClass());
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

    private static String buildCommonSql(Map<String, Object> params) {
        var where = (AbstractWhere<?, ?>) params.get(MybatisConsts.CHAIN);
        MybatisConsts.putEntityClass(params, where.entityClass());
        var pair = where.sql();
        params.put(MybatisConsts.SQL_PARAMS, pair.right);
        return pair.left;
    }
}