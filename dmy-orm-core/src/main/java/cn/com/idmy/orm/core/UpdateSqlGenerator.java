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
class UpdateSqlGenerator extends SqlGenerator {
    protected Updates<?> update;

    protected UpdateSqlGenerator(Updates<?> update) {
        super(update.entityClass);
        this.update = update;
    }

    protected Pair<String, List<Object>> gen() {
        var nodes = update.nodes;
        var sets = new ArrayList<Set>(nodes.size());
        var wheres = new ArrayList<Node>(nodes.size() - 1);
        for (int i = 0, size = nodes.size(); i < size; i++) {
            var node = nodes.get(i);
            if (node instanceof Set set) {
                sets.add(set);
            } else if (node instanceof Cond) {
                wheres.add(node);
            } else if (node instanceof Or) {
                skipAdjoinOr(node, wheres);
            }
        }
        sql.append(UPDATE).append(SqlConsts.STRESS_MARK).append(Tables.getTableName(update.entityClass)).append(SqlConsts.STRESS_MARK).append(SET);
        params = new ArrayList<>(update.sqlParamsSize);
        if (!sets.isEmpty()) {
            for (int i = 0, size = sets.size(); i < size; i++) {
                Set set = sets.get(i);
                builder(set);
                if (i < size - 1) {
                    if (sets.get(i + 1).type == Type.SET) {
                        sql.append(DELIMITER);
                    }
                }
            }
        }
        buildWhere(wheres);
        return Pair.of(sql.toString(), params);
    }
}