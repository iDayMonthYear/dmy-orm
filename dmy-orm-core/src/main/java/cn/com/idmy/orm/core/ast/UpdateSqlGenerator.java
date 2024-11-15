package cn.com.idmy.orm.core.ast;

import cn.com.idmy.orm.core.ast.Node.Cond;
import cn.com.idmy.orm.core.ast.Node.Or;
import cn.com.idmy.orm.core.ast.Node.Set;
import cn.com.idmy.orm.core.ast.Node.Type;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class UpdateSqlGenerator extends AbstractSqlGenerator {
    public static String gen(UpdateChain<?> update) {
        List<Node> nodes = update.nodes();
        List<Set> sets = new ArrayList<>(nodes.size());
        List<Node> wheres = new ArrayList<>(nodes.size());
        for (Node node : nodes) {
            if (node instanceof Set set) {
                sets.add(set);
            } else if (node instanceof Cond) {
                wheres.add(node);
            } else if (node instanceof Or) {
                skipAdjoinOr(node, wheres);
            }
        }

        StringBuilder sql = new StringBuilder("UPDATE ").append(getTableName(update.table())).append(" SET ");

        if (!sets.isEmpty()) {
            for (int i = 0, setsSize = sets.size(); i < setsSize; i++) {
                Set set = sets.get(i);
                sql.append(builder(set));
                if (i < setsSize - 1) {
                    Type type = sets.get(i + 1).type();
                    if (type == Type.SET) {
                        sql.append(", ");
                    }
                }
            }
        }

        buildWhere(wheres, sql);
        return sql.toString();
    }
}