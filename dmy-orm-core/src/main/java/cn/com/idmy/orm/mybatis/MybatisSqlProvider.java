package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.core.LambdaWhere;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.dromara.hutool.core.reflect.TypeUtil;

import java.util.Map;


public class MybatisSqlProvider {
    private static Class<?> getEntityClass(ProviderContext context) {
        return (Class<?>) TypeUtil.getTypeArgument(context.getMapperType());
    }

    private static String buildCommonSql(Map<String, Object> params, ProviderContext context) {
        var chain = (LambdaWhere<?, ?>) params.get(MybatisConsts.CHAIN);
        var pair = chain.sql();
        params.put(MybatisConsts.SQL_PARAMS, pair.right);
        params.put(MybatisConsts.ENTITY_CLASS, getEntityClass(context));
        return pair.left;
    }

    public String get(Map<String, Object> params, ProviderContext context) {
        return buildCommonSql(params, context);
    }

    public String find(Map<String, Object> params, ProviderContext context) {
        return buildCommonSql(params, context);
    }

    public String update(Map<String, Object> params, ProviderContext context) {
        return buildCommonSql(params, context);
    }

    public String delete(Map<String, Object> params, ProviderContext context) {
        return buildCommonSql(params, context);
    }

    public String insert(Map<String, Object> params, ProviderContext context) {
        return null;
    }

    public String inserts(Map<String, Object> params, ProviderContext context) {
        return null;
    }
}