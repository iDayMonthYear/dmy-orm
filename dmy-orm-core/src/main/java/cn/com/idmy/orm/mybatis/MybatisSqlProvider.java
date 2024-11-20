package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.core.LambdaWhere;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.dromara.hutool.core.reflect.TypeUtil;

import java.util.Map;


public class MybatisSqlProvider {
    private static Class<?> getEntityClass(ProviderContext context) {
        var mapperClass = context.getMapperType();
        return (Class<?>) TypeUtil.getTypeArgument(mapperClass);
    }

    private static String buildLambdaWhereSql(Map<String, Object> params) {
        var chain = (LambdaWhere<?, ?>) params.get(MybatisConsts.CHAIN);
        var pair = chain.sql();
        params.put(MybatisConsts.SQL_PARAMS, pair.right);
        return pair.left;
    }

    public String get(Map<String, Object> params) {
        return buildLambdaWhereSql(params);
    }

    public String find(Map<String, Object> params) {
        return buildLambdaWhereSql(params);
    }

    public String update(Map<String, Object> params) {
        return buildLambdaWhereSql(params);
    }

    public String delete(Map<String, Object> params) {
        return buildLambdaWhereSql(params);
    }

    public String insert(Map<String, Object> params, ProviderContext context) {
        return null;
    }

    public String inserts(Map<String, Object> params, ProviderContext context) {
        return null;
    }
}