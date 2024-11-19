package cn.com.idmy.orm.core.mybatis;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.ast.LambdaWhere;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "DuplicatedCode"})
public class MybatisSqlProvider {
    private static void setParams(Map<String, Object> params, Pair<String, List<Object>> pair) {
        params.put(MybatisConsts.SQL_PARAMS, pair.right);
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

    public String getById(Map<String, Object> params, ProviderContext context) {
        return null;
    }

    public String find(Map<String, Object> params) {
        return lambdaWhere(params);
    }

    public String findByIds(Map<String, Object> params, ProviderContext context) {
        return null;
    }

    public String update(Map<String, Object> params) {
        return lambdaWhere(params);
    }

    public String delete(Map<String, Object> params) {
        return lambdaWhere(params);
    }

    public String deleteById(Map<String, Object> params, ProviderContext context) {
        return null;
    }

    public String deleteByIds(Map<String, Object> params, ProviderContext context) {
        return null;
    }
}