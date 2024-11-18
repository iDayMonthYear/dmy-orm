package cn.com.idmy.orm.core.mybatis;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.ast.DeleteChain;
import cn.com.idmy.orm.core.ast.SelectChain;
import cn.com.idmy.orm.core.ast.UpdateChain;

import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "DuplicatedCode"})
public class MybatisSqlProvider {
    private static void setParams(Map<String, Object> params, Pair<String, List<Object>> pair) {
        params.put(MybatisConsts.SQL_ARGS, pair.right);
    }

    public String get(Map<String, Object> params) {
        SelectChain<?> chain = (SelectChain<?>) params.get(MybatisConsts.SELECT);
        Pair<String, List<Object>> pair = chain.sql();
        setParams(params, pair);
        return pair.left;
    }

    public String find(Map<String, Object> params) {
        SelectChain<?> chain = (SelectChain<?>) params.get(MybatisConsts.SELECT);
        Pair<String, List<Object>> pair = chain.sql();
        setParams(params, pair);
        return pair.left;
    }

    public String update(Map<String, Object> params) {
        UpdateChain<?> chain = (UpdateChain<?>) params.get("chain");
        Pair<String, List<Object>> pair = chain.sql();
        setParams(params, pair);
        return pair.left;
    }

    public String delete(Map<String, Object> params) {
        DeleteChain<?> chain = (DeleteChain<?>) params.get("chain");
        Pair<String, List<Object>> pair = chain.sql();
        setParams(params, pair);
        return pair.left;
    }
}