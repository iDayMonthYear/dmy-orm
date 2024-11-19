package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.Node.Cond;
import cn.com.idmy.orm.core.Node.Or;
import cn.com.idmy.orm.util.OrmUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static cn.com.idmy.orm.core.SqlConsts.DELETE;
import static cn.com.idmy.orm.core.SqlConsts.FROM;

@Slf4j
public class DeleteSqlGenerator extends AbstractSqlGenerator {
    public static Pair<String, List<Object>> gen(DeleteChain<?> chain) {
        List<Node> nodes = chain.nodes();
        List<Node> wheres = new ArrayList<>(nodes.size());
        for (Node node : nodes) {
            if (node instanceof Cond) {
                wheres.add(node);
            } else if (node instanceof Or) {
                skipAdjoinOr(node, wheres);
            }
        }
        List<Object> params = new ArrayList<>(chain.sqlParamsSize());
        StringBuilder sql = new StringBuilder(DELETE).append(FROM).append(OrmUtil.getTableName(chain.entityClass()));
        buildWhere(wheres, sql, params);
        return Pair.of(sql.toString(), params);
    }
}