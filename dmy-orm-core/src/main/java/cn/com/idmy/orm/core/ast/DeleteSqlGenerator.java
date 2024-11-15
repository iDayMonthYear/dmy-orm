package cn.com.idmy.orm.core.ast;

import cn.com.idmy.orm.core.ast.Node.Cond;
import cn.com.idmy.orm.core.ast.Node.Or;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DeleteSqlGenerator extends AbstractSqlGenerator {
    public static String gen(DeleteChain<?> deleteChain) {
        List<Node> nodes = deleteChain.nodes();
        List<Node> wheres = new ArrayList<>(nodes.size());
        for (Node node : nodes) {
            if (node instanceof Cond) {
                wheres.add(node);
            } else if (node instanceof Or) {
                skipAdjoinOr(node, wheres);
            }
        }

        StringBuilder sql = new StringBuilder("delete from ").append(getTableName(deleteChain.table()));
        buildWhere(wheres, sql);
        return sql.toString();
    }
}