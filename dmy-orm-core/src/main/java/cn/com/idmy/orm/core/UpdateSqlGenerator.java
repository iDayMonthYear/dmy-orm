package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.SqlNode.SqlCond;
import cn.com.idmy.orm.core.SqlNode.SqlNodeType;
import cn.com.idmy.orm.core.SqlNode.SqlOr;
import cn.com.idmy.orm.core.SqlNode.SqlSet;
import cn.com.idmy.orm.mybatis.handler.TypeHandlerValue;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.collection.CollUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static cn.com.idmy.orm.core.SqlConsts.DELIMITER;
import static cn.com.idmy.orm.core.SqlConsts.EQUAL;
import static cn.com.idmy.orm.core.SqlConsts.SET;
import static cn.com.idmy.orm.core.SqlConsts.UPDATE;

@Slf4j
class UpdateSqlGenerator extends SqlGenerator {
    protected Update<?> update;

    protected UpdateSqlGenerator(Update<?> update) {
        super(update.entityClass, update.nodes);
        this.update = update;
    }

    @Override
    protected @NotNull Pair<String, List<Object>> doGenerate() {
        var sets = new ArrayList<SqlSet>(nodes.size());
        var wheres = new ArrayList<SqlNode>(nodes.size() - 1);
        for (var node : nodes) {
            if (node instanceof SqlSet set) {
                sets.add(set);
            } else if (node instanceof SqlCond) {
                wheres.add(node);
            } else if (node instanceof SqlOr) {
                skipAdjoinOr(node, wheres);
            }
        }

        sql.append(UPDATE).append(SqlConsts.STRESS_MARK).append(tableName).append(SqlConsts.STRESS_MARK).append(SET);
        params = new ArrayList<>(update.sqlParamsSize);

        if (!sets.isEmpty()) {
            for (int i = 0, size = sets.size(); i < size; i++) {
                genSet(sets.get(i));
                if (i < size - 1 && sets.get(i + 1).type == SqlNodeType.SET) {
                    sql.append(DELIMITER);
                }
            }
        }
        genWhere(wheres);
        return Pair.of(sql.toString(), params);
    }

    protected void genSet(@NotNull SqlSet set) {
        var col = set.column;
        var expr = genSqlExpr(col, set.expr, null);
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