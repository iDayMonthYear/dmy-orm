package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.SqlNode.SqlOr;
import cn.com.idmy.orm.core.SqlNode.SqlWhere;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


@Slf4j
class DeleteSqlGenerator extends SqlGenerator {
    protected @NotNull Delete<?> delete;

    protected DeleteSqlGenerator(@NotNull Delete<?> d) {
        super(d.entityType, d.nodes);
        this.delete = d;
    }

    @Override
    protected @NotNull Pair<String, List<Object>> doGenerate() {
        if (!delete.hasWhere && !delete.force) {
            throw new OrmException("删除语句没有条件！使用 force() 强制删除全部数据");
        }
        var wheres = new ArrayList<SqlNode>(nodes.size());
        for (int i = 0, size = nodes.size(); i < size; i++) {
            var node = nodes.get(i);
            if (node instanceof SqlWhere) {
                wheres.add(node);
            } else if (node instanceof SqlOr) {
                skipAdjoinOr(node, wheres);
            }
        }

        sql.append(DELETE_FROM).append(tableInfo.schema()).append(STRESS_MARK).append(tableInfo.name()).append(STRESS_MARK);
        values = new ArrayList<>(delete.sqlParamsSize);

        genWhere(wheres);
        return new Pair<>(sql.toString(), values);
    }
}