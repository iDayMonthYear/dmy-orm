package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.Node.Cond;
import cn.com.idmy.orm.core.Node.Or;
import cn.com.idmy.orm.core.Node.Set;
import cn.com.idmy.orm.core.Node.Type;
import cn.com.idmy.orm.util.OrmUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static cn.com.idmy.orm.core.SqlConsts.DELIMITER;
import static cn.com.idmy.orm.core.SqlConsts.SET;
import static cn.com.idmy.orm.core.SqlConsts.UPDATE;

@Slf4j
public class UpdateSqlGenerator extends AbstractSqlGenerator {
    public static Pair<String, List<Object>> gen(UpdateChain<?> update) {
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
        StringBuilder sql = new StringBuilder(UPDATE).append(OrmUtil.getTableName(update.entityClass())).append(SET);
        List<Object> params = new ArrayList<>();
        if (!sets.isEmpty()) {
            for (int i = 0, setsSize = sets.size(); i < setsSize; i++) {
                Set set = sets.get(i);
                builder(set, sql, params);
                if (i < setsSize - 1) {
                    Type type = sets.get(i + 1).type();
                    if (type == Type.SET) {
                        sql.append(DELIMITER);
                    }
                }
            }
        }
        buildWhere(wheres, sql, params);
        return Pair.of(sql.toString(), params);
    }
}