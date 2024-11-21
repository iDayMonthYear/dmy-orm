package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.Node.Cond;
import cn.com.idmy.orm.core.Node.Or;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static cn.com.idmy.orm.core.SqlConsts.DELETE;
import static cn.com.idmy.orm.core.SqlConsts.FROM;

@Slf4j
class DeleteSqlGenerator extends AbstractSqlGenerator {
    public static Pair<String, List<Object>> gen(DeleteChain<?> chain) {
        var nodes = chain.nodes();
        var wheres = new ArrayList<Node>(nodes.size());
        for (var node : nodes) {
            if (node instanceof Cond) {
                wheres.add(node);
            } else if (node instanceof Or) {
                skipAdjoinOr(node, wheres);
            }
        }
        var params = new ArrayList<>(chain.sqlParamsSize());
        var sql = new StringBuilder(DELETE).append(FROM).append(SqlConsts.STRESS_MARK).append(TableManager.getTableName(chain.entityClass())).append(SqlConsts.STRESS_MARK);
        buildWhere(wheres, sql, params);
        return Pair.of(sql.toString(), params);
    }
}