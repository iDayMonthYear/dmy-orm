package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.Node.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cn.com.idmy.orm.core.SqlConsts.ASTERISK;
import static cn.com.idmy.orm.core.SqlConsts.DELIMITER;
import static cn.com.idmy.orm.core.SqlConsts.FROM;
import static cn.com.idmy.orm.core.SqlConsts.GROUP_BY;
import static cn.com.idmy.orm.core.SqlConsts.LIMIT;
import static cn.com.idmy.orm.core.SqlConsts.OFFSET;
import static cn.com.idmy.orm.core.SqlConsts.ORDER_BY;
import static cn.com.idmy.orm.core.SqlConsts.SELECT;

@Slf4j
class SelectSqlGenerator extends SqlGenerator {
    protected Selects<?> select;

    protected SelectSqlGenerator(Selects<?> select) {
        super(select.entityClass);
        this.select = select;
    }

    protected Pair<String, List<Object>> gen() {
        var nodes = select.nodes;
        var selectColumns = new ArrayList<SelectColumn>(1);
        var wheres = new ArrayList<Node>(nodes.size());
        var groups = new ArrayList<GroupBy>(1);
        var orders = new ArrayList<OrderBy>(2);
        Distinct distinct = null;
        for (int i = 0, size = nodes.size(); i < size; i++) {
            switch (nodes.get(i)) {
                case Cond cond -> wheres.add(cond);
                case SelectColumn selectColumn -> selectColumns.add(selectColumn);
                case GroupBy groupBy -> groups.add(groupBy);
                case OrderBy orderBy -> orders.add(orderBy);
                case Or or -> skipAdjoinOr(or, wheres);
                case Distinct d -> distinct = d;
                case null, default -> {
                }
            }
        }
        sql.append(SELECT);
        params = new ArrayList<>(select.sqlParamsSize);
        if (distinct != null) {
            builder(distinct);
            if (!selectColumns.isEmpty()) {
                sql.append(DELIMITER);
            }
        }
        buildSelectColumn(selectColumns);
        sql.append(FROM).append(SqlConsts.STRESS_MARK).append(Tables.getTableName(select.entityClass)).append(SqlConsts.STRESS_MARK);
        buildWhere(wheres);
        buildGroupBy(groups);
        buildOrderBy(orders);
        if (select.limit != null) {
            sql.append(LIMIT).append(select.limit);
        }
        if (select.offset != null) {
            sql.append(OFFSET).append(select.offset);
        }
        return Pair.of(sql.toString(), params);
    }

    protected void buildSelectColumn(List<SelectColumn> selectColumns) {
        if (selectColumns.isEmpty()) {
            sql.append(ASTERISK);
        } else {
            Set<String> set = new HashSet<>(selectColumns.size());
            for (int i = 0, size = selectColumns.size(); i < size; i++) {
                var selectColumn = selectColumns.get(i);
                var column = (String) builder(selectColumn);
                if (log.isWarnEnabled()) {
                    if (set.contains(column)) {
                        log.error("select {} 列名重复会导致映射到实体类异常", column);
                    }
                    set.add(column);
                }
                if (i < size - 1) {
                    if (selectColumns.get(i + 1).type == Type.SELECT_COLUMN) {
                        sql.append(DELIMITER);
                    }
                }
            }
        }
    }

    protected void buildGroupBy(List<GroupBy> groups) {
        if (!groups.isEmpty()) {
            sql.append(GROUP_BY);
            for (int i = 0, size = groups.size(); i < size; i++) {
                GroupBy group = groups.get(i);
                builder(group);
                if (i < size - 1) {
                    if (groups.get(i + 1).type == Type.GROUP_BY) {
                        sql.append(DELIMITER);
                    }
                }
            }
        }
    }

    private void buildOrderBy(List<OrderBy> orders) {
        if (!orders.isEmpty()) {
            sql.append(ORDER_BY);
            for (int i = 0, size = orders.size(); i < size; i++) {
                OrderBy order = orders.get(i);
                builder(order);
                if (i < size - 1) {
                    if (orders.get(i + 1).type == Type.ORDER_BY) {
                        sql.append(DELIMITER);
                    }
                }
            }
        }
    }
}