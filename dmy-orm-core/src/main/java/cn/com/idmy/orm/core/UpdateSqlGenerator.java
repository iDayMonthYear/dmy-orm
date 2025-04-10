package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.SqlNode.*;
import cn.com.idmy.orm.mybatis.handler.TypeHandlerValue;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


@Slf4j
class UpdateSqlGenerator extends SqlGenerator {
    protected final @NotNull Update<?> update;

    protected UpdateSqlGenerator(@NotNull Update<?> u) {
        super(u.entityType, u.nodes);
        this.update = u;
    }

    @Override
    protected @NotNull Pair<String, List<Object>> doGenerate() {
        if (!update.hasCond && !update.force) {
            throw new OrmException("更新语句没有条件！使用 force() 强制更新全部数据");
        }
        var sets = new ArrayList<SqlSet>(nodes.size());
        var wheres = new ArrayList<SqlNode>(nodes.size() - 1);
        for (int i = 0, size = nodes.size(); i < size; i++) {
            var node = nodes.get(i);
            if (node instanceof SqlSet set) {
                sets.add(set);
            } else if (node instanceof SqlCond || node instanceof SqlBracket) {
                wheres.add(node);
            } else if (node == SqlOr.OR) {
                skipAdjoinOr(wheres);
            }
        }

        sql.append(UPDATE).append(tableInfo.schema()).append(STRESS_MARK).append(tableInfo.name()).append(STRESS_MARK).append(SET);
        values = new ArrayList<>(update.sqlParamsSize);

        if (!sets.isEmpty()) {
            for (int i = 0, size = sets.size(); i < size; i++) {
                genSet(sets.get(i));
                if (i < size - 1 && sets.get(i + 1).type == Type.SET) {
                    sql.append(DELIMITER);
                }
            }
        }
        genWhere(wheres);
        return new Pair<>(sql.toString(), values);
    }

    protected String genSet(@NonNull String col, @Nullable Object val) {
        if (val instanceof SqlOpExpr expr) {
            var sqlOp = expr.op(new SqlOp<>());
            values.add(sqlOp.value());
            return keyword(col) + BLANK + sqlOp.op() + BLANK + PLACEHOLDER;
        } else {
            values.add(val);
            return PLACEHOLDER;
        }
    }

    protected void genSet(@NotNull SqlNode.SqlSet set) {
        var col = set.column;
        var expr = genSet(col, set.expr);
        var th = Tables.getTypeHandler(set.field());
        if (th != null) {
            values.add(new TypeHandlerValue(th, values.removeLast()));
        }
        sql.append(keyword(col)).append(EQUAL).append(expr);
    }
}