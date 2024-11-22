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
class SelectSqlGenerator extends AbstractSqlGenerator {
    public static Pair<String, List<Object>> gen(SelectChain<?> chain) {
        var nodes = chain.nodes();
        var selectColumns = new ArrayList<SelectColumn>(nodes.size());
        var wheres = new ArrayList<Node>(nodes.size());
        var groups = new ArrayList<GroupBy>(1);
        var orders = new ArrayList<OrderBy>(2);
        Distinct distinct = null;
        for (Node node : nodes) {
            switch (node) {
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

        var params = new ArrayList<>(chain.sqlParamsSize());
        var sql = new StringBuilder(SELECT);
        if (distinct != null) {
            builder(distinct, sql, params);
            if (!selectColumns.isEmpty()) {
                sql.append(DELIMITER);
            }
        }
        buildSelectColumn(selectColumns, sql, params);
        sql.append(FROM).append(SqlConsts.STRESS_MARK).append(TableManager.getTableName(chain.entityClass())).append(SqlConsts.STRESS_MARK);
        buildWhere(wheres, sql, params);
        buildGroupBy(groups, sql, params);
        buildOrderBy(orders, sql, params);
        if (chain.limit != null) {
            sql.append(LIMIT).append(chain.limit);
        }
        if (chain.offset != null) {
            sql.append(OFFSET).append(chain.offset);
        }
        return Pair.of(sql.toString(), params);
    }

    private static void buildSelectColumn(List<SelectColumn> selectColumns, StringBuilder sql, List<Object> params) {
        if (selectColumns.isEmpty()) {
            sql.append(ASTERISK);
        } else {
            Set<String> set = new HashSet<>(selectColumns.size());
            for (int i = 0, size = selectColumns.size(); i < size; i++) {
                var selectColumn = selectColumns.get(i);
                var column = (String) builder(selectColumn, sql, params);
                if (log.isDebugEnabled()) {
                    if (set.contains(column)) {
                        log.error("select {} 列名重复会导致映射到实体类异常", column);
                    }
                    set.add(column);
                }
                if (i < size - 1) {
                    Type type = selectColumns.get(i + 1).type();
                    if (type == Type.SELECT_COLUMN) {
                        sql.append(DELIMITER);
                    }
                }
            }
        }
    }

    private static void buildGroupBy(List<GroupBy> groups, StringBuilder sql, List<Object> params) {
        if (!groups.isEmpty()) {
            sql.append(GROUP_BY);
            for (int i = 0, size = groups.size(); i < size; i++) {
                GroupBy group = groups.get(i);
                builder(group, sql, params);
                if (i < size - 1) {
                    Type type = groups.get(i + 1).type();
                    if (type == Type.GROUP_BY) {
                        sql.append(DELIMITER);
                    }
                }
            }
        }
    }

    private static void buildOrderBy(List<OrderBy> orders, StringBuilder sql, List<Object> params) {
        if (!orders.isEmpty()) {
            sql.append(ORDER_BY);
            for (int i = 0, size = orders.size(); i < size; i++) {
                OrderBy order = orders.get(i);
                builder(order, sql, params);
                if (i < size - 1) {
                    Type type = orders.get(i + 1).type();
                    if (type == Type.ORDER_BY) {
                        sql.append(DELIMITER);
                    }
                }
            }
        }
    }
}