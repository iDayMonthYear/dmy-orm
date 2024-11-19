package cn.com.idmy.orm.mybatis;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.LambdaWhere;
import cn.com.idmy.orm.core.SqlConsts;
import cn.com.idmy.orm.util.OrmUtil;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.reflect.TypeUtil;

import java.util.*;

@SuppressWarnings({"rawtypes", "DuplicatedCode"})
public class MybatisSqlProvider {
    private static void setParams(Map<String, Object> params, Pair<String, List<Object>> pair) {
        params.put(MybatisConsts.SQL_PARAMS, pair.right);
    }

    private static Class<?> getEntityClass(ProviderContext context) {
        Class<?> mapperClass = context.getMapperType();
        return (Class<?>) TypeUtil.getTypeArgument(mapperClass);
    }

    private static String lambdaWhere(Map<String, Object> params) {
        LambdaWhere chain = (LambdaWhere) params.get(MybatisConsts.CHAIN);
        Pair<String, List<Object>> pair = chain.sql();
        setParams(params, pair);
        return pair.left;
    }

    public String get(Map<String, Object> params) {
        return lambdaWhere(params);
    }

    public String find(Map<String, Object> params) {
        return lambdaWhere(params);
    }

    public String update(Map<String, Object> params) {
        return lambdaWhere(params);
    }

    public String delete(Map<String, Object> params) {
        return lambdaWhere(params);
    }

    public String insert(Map<String, Object> params, ProviderContext context) {
        Class<?> entityClass = getEntityClass(context);
        Object entity = params.get("entity");
        List<String> columns = OrmUtil.findFields(entityClass, entity);
        List<Object> values = OrmUtil.findValues(entityClass, entity);
        params.put(MybatisConsts.SQL_PARAMS, values);

        return "INSERT INTO " +
                OrmUtil.getTableName(entityClass) +
                SqlConsts.BRACKET_LEFT +
                String.join(SqlConsts.DELIMITER, columns) +
                SqlConsts.BRACKET_RIGHT +
                " VALUES " +
                SqlConsts.BRACKET_LEFT +
                String.join(SqlConsts.DELIMITER, Collections.nCopies(columns.size(), SqlConsts.PLACEHOLDER)) +
                SqlConsts.BRACKET_RIGHT;
    }

    public String inserts(Map<String, Object> params, ProviderContext context) {
        Class<?> entityClass = getEntityClass(context);
        Collection<?> entities = (Collection<?>) params.get("entities");
        if (CollUtil.isEmpty(entities)) {
            throw new OrmException("Batch insert entities cannot be empty");
        }

        List<String> columns = OrmUtil.findFields(entityClass, entities.iterator().next());
        List<Object> values = new ArrayList<>();
        StringBuilder sql = new StringBuilder("INSERT INTO ")
                .append(OrmUtil.getTableName(entityClass))
                .append(SqlConsts.BRACKET_LEFT)
                .append(String.join(SqlConsts.DELIMITER, columns))
                .append(SqlConsts.BRACKET_RIGHT)
                .append(" VALUES ");

        String placeholder = SqlConsts.BRACKET_LEFT + String.join(SqlConsts.DELIMITER, Collections.nCopies(columns.size(), SqlConsts.PLACEHOLDER)) + SqlConsts.BRACKET_RIGHT;

        int i = 0;
        for (Object entity : entities) {
            if (i > 0) {
                sql.append(SqlConsts.DELIMITER);
            }
            sql.append(placeholder);
            values.addAll(OrmUtil.findValues(entityClass, entity));
            i++;
        }

        params.put(MybatisConsts.SQL_PARAMS, values);
        return sql.toString();
    }

    public String updateById(Map<String, Object> params, ProviderContext context) {
        Class<?> entityClass = getEntityClass(context);
        Object entity = params.get("entity");
        List<String> columns = OrmUtil.findFields(entityClass, entity);
        List<Object> values = OrmUtil.findValues(entityClass, entity);

        StringBuilder sql = new StringBuilder(SqlConsts.UPDATE)
                .append(OrmUtil.getTableName(entityClass))
                .append(SqlConsts.SET);

        for (int i = 0; i < columns.size(); i++) {
            if (i > 0) {
                sql.append(SqlConsts.DELIMITER);
            }
            sql.append(columns.get(i))
                    .append(SqlConsts.EQUALS_PLACEHOLDER);
        }

        sql.append(SqlConsts.WHERE)
                .append(OrmUtil.getId(entityClass))
                .append(SqlConsts.EQUALS_PLACEHOLDER);

        values.add(OrmUtil.getIdValue(entity));
        params.put(MybatisConsts.SQL_PARAMS, values);
        return sql.toString();
    }
}