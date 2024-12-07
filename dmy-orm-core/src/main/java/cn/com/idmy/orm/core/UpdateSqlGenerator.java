package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.Node.Cond;
import cn.com.idmy.orm.core.Node.Or;
import cn.com.idmy.orm.core.Node.Set;
import cn.com.idmy.orm.core.Node.Type;
import cn.com.idmy.orm.mybatis.handler.TypeHandlerValue;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.collection.CollUtil;

import java.util.ArrayList;
import java.util.List;

import static cn.com.idmy.orm.core.SqlConsts.DELIMITER;
import static cn.com.idmy.orm.core.SqlConsts.EQUAL;
import static cn.com.idmy.orm.core.SqlConsts.SET;
import static cn.com.idmy.orm.core.SqlConsts.UPDATE;

@Slf4j
class UpdateSqlGenerator extends SqlGenerator {
    protected Updates<?> update;

    protected UpdateSqlGenerator(Updates<?> update) {
        super(update.entityClass, update.nodes);
        this.update = update;
    }

    @Override
    protected Pair<String, List<Object>> doGenerate() {
        var sets = new ArrayList<Set>(nodes.size());
        var wheres = new ArrayList<Node>(nodes.size() - 1);
        for (var node : nodes) {
            if (node instanceof Set set) {
                sets.add(set);
            } else if (node instanceof Cond) {
                wheres.add(node);
            } else if (node instanceof Or) {
                skipAdjoinOr(node, wheres);
            }
        }

        sql.append(UPDATE).append(SqlConsts.STRESS_MARK).append(tableName).append(SqlConsts.STRESS_MARK).append(SET);
        params = new ArrayList<>(update.sqlParamsSize);

        if (!sets.isEmpty()) {
            for (int i = 0, size = sets.size(); i < size; i++) {
                buildSet(sets.get(i));
                if (i < size - 1 && sets.get(i + 1).type == Type.SET) {
                    sql.append(DELIMITER);
                }
            }
        }
        buildWhere(wheres);
        return Pair.of(sql.toString(), params);
    }

    protected void buildSet(Set set) {
        var col = set.column;
        var expr = buildSqlExpr(col, set.expr, null);
        var map = Tables.getTable(entityClass).columnMap();
        if (CollUtil.isNotEmpty(map)) {
            var th = map.get(col).typeHandler();
            if (th != null) {
                var val = params.removeLast();
                params.add(new TypeHandlerValue(th, val));
            }
        }
        sql.append(warpKeyword(col)).append(EQUAL).append(expr);
    }
}