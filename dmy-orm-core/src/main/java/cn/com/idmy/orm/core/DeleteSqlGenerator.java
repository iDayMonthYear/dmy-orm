package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.Node.Cond;
import cn.com.idmy.orm.core.Node.Or;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static cn.com.idmy.orm.core.SqlConsts.DELETE_FROM;

@Slf4j
class DeleteSqlGenerator extends SqlGenerator {
    protected Deletes<?> delete;
    protected DeleteSqlGenerator(Deletes<?> delete) {
        super(delete.entityClass);
        this.delete = delete;
    }

    protected Pair<String, List<Object>> gen() {
        var nodes = delete.nodes;
        var wheres = new ArrayList<Node>(nodes.size());
        for (int i = 0, size = nodes.size(); i < size; i++) {
            var node = nodes.get(i);
            if (node instanceof Cond) {
                wheres.add(node);
            } else if (node instanceof Or) {
                skipAdjoinOr(node, wheres);
            }
        }
        sql.append(DELETE_FROM).append(SqlConsts.STRESS_MARK).append(Tables.getTableName(delete.entityClass)).append(SqlConsts.STRESS_MARK);
        params = new ArrayList<>(delete.sqlParamsSize);
        buildWhere(wheres);
        return Pair.of(sql.toString(), params);
    }
}