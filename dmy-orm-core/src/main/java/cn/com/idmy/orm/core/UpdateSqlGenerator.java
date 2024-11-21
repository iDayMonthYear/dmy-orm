package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.Node.Cond;
import cn.com.idmy.orm.core.Node.Or;
import cn.com.idmy.orm.core.Node.Set;
import cn.com.idmy.orm.core.Node.Type;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static cn.com.idmy.orm.core.SqlConsts.DELIMITER;
import static cn.com.idmy.orm.core.SqlConsts.SET;
import static cn.com.idmy.orm.core.SqlConsts.UPDATE;

@Slf4j
class UpdateSqlGenerator extends AbstractSqlGenerator {
    public static Pair<String, List<Object>> gen(UpdateChain<?> chain) {
        var nodes = chain.nodes();
        var sets = new ArrayList<Set>(nodes.size());
        var wheres = new ArrayList<Node>(nodes.size());
        for (var node : nodes) {
            if (node instanceof Set set) {
                sets.add(set);
            } else if (node instanceof Cond) {
                wheres.add(node);
            } else if (node instanceof Or) {
                skipAdjoinOr(node, wheres);
            }
        }
        var sql = new StringBuilder(UPDATE).append(SqlConsts.STRESS_MARK).append(TableManager.getTableName(chain.entityClass())).append(SqlConsts.STRESS_MARK).append(SET);
        var params = new ArrayList<>(chain.sqlParamsSize());
        if (!sets.isEmpty()) {
            for (int i = 0, size = sets.size(); i < size; i++) {
                Set set = sets.get(i);
                builder(set, sql, params);
                if (i < size - 1) {
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