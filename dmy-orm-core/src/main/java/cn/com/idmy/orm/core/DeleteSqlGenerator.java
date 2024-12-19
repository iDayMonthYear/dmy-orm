package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.SqlNode.SqlCond;
import cn.com.idmy.orm.core.SqlNode.SqlOr;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static cn.com.idmy.orm.core.SqlConsts.DELETE_FROM;

@Slf4j
class DeleteSqlGenerator extends SqlGenerator {
    protected Delete<?> delete;

    protected DeleteSqlGenerator(Delete<?> delete) {
        super(delete.entityClass, delete.nodes);
        this.delete = delete;
    }

    @Override
    protected Pair<String, List<Object>> doGenerate() {
        var wheres = new ArrayList<SqlNode>(nodes.size());
        for (var node : nodes) {
            if (node instanceof SqlCond) {
                wheres.add(node);
            } else if (node instanceof SqlOr) {
                skipAdjoinOr(node, wheres);
            }
        }

        sql.append(DELETE_FROM).append(SqlConsts.STRESS_MARK).append(tableName).append(SqlConsts.STRESS_MARK);
        params = new ArrayList<>(delete.sqlParamsSize);

        genWhere(wheres);
        return Pair.of(sql.toString(), params);
    }
}