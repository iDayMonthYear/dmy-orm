package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.SqlNode.Cond;
import cn.com.idmy.orm.core.SqlNode.Or;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


@Slf4j
class DeleteSqlGenerator extends SqlGenerator {
    @NotNull
    protected Delete<?> delete;

    protected DeleteSqlGenerator(@NotNull Delete<?> d) {
        super(d.entityType, d.nodes);
        this.delete = d;
    }

    @Override
    protected @NotNull Pair<String, List<Object>> doGen() {
        var wheres = new ArrayList<SqlNode>(nodes.size());
        for (int i = 0, size = nodes.size(); i < size; i++) {
            var node = nodes.get(i);
            if (node instanceof Cond) {
                wheres.add(node);
            } else if (node instanceof Or) {
                skipAdjoinOr(node, wheres);
            }
        }

        sql.append(DELETE_FROM).append(STRESS_MARK).append(tableName).append(STRESS_MARK);
        params = new ArrayList<>(delete.sqlParamsSize);

        boolean empty = genWhere(wheres);
        if (empty && !delete.force) {
            throw new OrmException("删除语句没有条件！可使用 force 强制执行");
        } else {
            return Pair.of(sql.toString(), params);
        }
    }
}