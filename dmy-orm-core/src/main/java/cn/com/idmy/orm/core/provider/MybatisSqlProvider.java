package cn.com.idmy.orm.core.provider;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.ast.DeleteChain;
import cn.com.idmy.orm.core.ast.SelectChain;
import cn.com.idmy.orm.core.ast.UpdateChain;

import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "DuplicatedCode"})
public class MybatisSqlProvider {
    public String get(Map<String, Object> params) {
        SelectChain<?> chain = (SelectChain<?>) params.get("chain");
        Pair<String, List<Object>> pair = chain.sql();
        return pair.left;
    }

    public String find(Map<String, Object> params) {
        SelectChain<?> chain = (SelectChain<?>) params.get("chain");
        Pair<String, List<Object>> pair = chain.sql();
        return pair.left;
    }

    public String update(Map<String, Object> params) {
        UpdateChain<?> chain = (UpdateChain<?>) params.get("chain");
        Pair<String, List<Object>> pair = chain.sql();
        return pair.left;
    }

    public String delete(Map<String, Object> params) {
        DeleteChain<?> chain = (DeleteChain<?>) params.get("chain");
        Pair<String, List<Object>> pair = chain.sql();
        return pair.left;
    }
}
