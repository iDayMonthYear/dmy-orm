package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.SqlNode.SqlCond;
import cn.com.idmy.orm.core.SqlNode.SqlNodeType;
import cn.com.idmy.orm.core.SqlNode.SqlOr;
import cn.com.idmy.orm.core.SqlNode.SqlSet;
import cn.com.idmy.orm.mybatis.handler.TypeHandlerValue;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


@Slf4j
class UpdateSqlGenerator extends SqlGenerator {
    protected final Update<?> update;

    protected UpdateSqlGenerator(Update<?> u) {
        super(u.entityType, u.nodes);
        this.update = u;
    }

    @Override
    protected @NotNull Pair<String, List<Object>> doGen() {
        var sets = new ArrayList<SqlSet>(nodes.size());
        var wheres = new ArrayList<SqlNode>(nodes.size() - 1);
        for (int i = 0, size = nodes.size(); i < size; i++) {
            var node = nodes.get(i);
            if (node instanceof SqlSet set) {
                sets.add(set);
            } else if (node instanceof SqlCond cond) {
                wheres.add(cond);
            } else if (node instanceof SqlOr) {
                skipAdjoinOr(node, wheres);
            }
        }

        sql.append(UPDATE).append(STRESS_MARK).append(tableName).append(STRESS_MARK).append(SET);
        params = new ArrayList<>(update.sqlParamsSize);

        if (!sets.isEmpty()) {
            for (int i = 0, size = sets.size(); i < size; i++) {
                genSet(sets.get(i));
                if (i < size - 1 && sets.get(i + 1).type == SqlNodeType.SET) {
                    sql.append(DELIMITER);
                }
            }
        }

        boolean empty = genWhere(wheres);
        if (empty && !update.force) {
            throw new IllegalArgumentException("更新语句没有条件！可使用 force 强制执行");
        } else {
            return new Pair<>(sql.toString(), params);
        }
    }

    protected String genSet(@NonNull String col, @NonNull SqlOpExpr expr) {
        var sqlOp = expr.op(new SqlOp<>());
        params.add(sqlOp.value());
        return keyword(col) + BLANK + sqlOp.op() + BLANK + PLACEHOLDER;
    }

    protected String genSet(@NonNull String col, @Nullable Object val) {
        params.add(val);
        return PLACEHOLDER;
    }

    protected void genSet(@NotNull SqlSet set) {
        var col = set.column;
        var expr = genSet(col, set.expr);
        var colum = Tables.getColum(entityType, col);
        if (colum != null) {
            var th = Tables.getTypeHandler(colum.field());
            if (th != null) {
                var val = params.removeLast();
                params.add(new TypeHandlerValue(th, val));
            }
        }
        sql.append(keyword(col)).append(EQUAL).append(expr);
    }
}