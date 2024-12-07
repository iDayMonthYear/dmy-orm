package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.Node.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static cn.com.idmy.orm.core.SqlConsts.*;

@Slf4j
class SelectSqlGenerator extends SqlGenerator {
    protected Selects<?> select;

    protected SelectSqlGenerator(Selects<?> select) {
        super(select);
        this.select = select;
    }

    @Override
    protected Pair<String, List<Object>> generate() {
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
            buildDistinct(distinct);
            if (!selectColumns.isEmpty()) {
                sql.append(DELIMITER);
            }
        }

        buildSelectColumn(selectColumns);
        sql.append(FROM).append(SqlConsts.STRESS_MARK).append(tableName).append(SqlConsts.STRESS_MARK);
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

    protected void buildDistinct(Distinct distinct) {
        var col = distinct.column;
        if (col == null) {
            sql.append(DISTINCT);
        } else {
            sql.append(DISTINCT).append(BRACKET_LEFT).append(warpKeyword(col)).append(BRACKET_RIGHT);
        }
    }

    protected String buildSelectColumn(SelectColumn sc) {
        var col = warpKeyword(sc.column);
        if (sc.expr == null) {
            sql.append(col);
        } else {
            var expr = sc.expr;
            var fn = expr.apply();
            var name = fn.name();
            if (name == SqlFnName.IF_NULL) {
                sql.append(name.getName()).append(BRACKET_LEFT).append(col).append(DELIMITER).append(PLACEHOLDER).append(BRACKET_RIGHT).append(BLANK).append(col);
                params.add(fn.value());
            } else {
                sql.append(name.getName()).append(BRACKET_LEFT).append(col).append(BRACKET_RIGHT).append(BLANK).append(col);
            }
        }
        return sc.column;
    }

    protected void buildSelectColumn(List<SelectColumn> scs) {
        if (scs.isEmpty()) {
            sql.append(ASTERISK);
        } else {
            var set = new HashSet<>(scs.size());
            for (int i = 0, size = scs.size(); i < size; i++) {
                var sc = scs.get(i);
                var col = buildSelectColumn(sc);
                if (set.contains(col)) {
                    throw new OrmException("select " + col + " 列名重复会导致映射到实体类异常");
                } else {
                    set.add(col);
                    if (i < size - 1 && scs.get(i + 1).type == Type.SELECT_COLUMN) {
                        sql.append(DELIMITER);
                    }
                }
            }
        }
    }

    protected void buildGroupBy(GroupBy group) {
        sql.append(warpKeyword(group.column));
    }

    protected void buildGroupBy(List<GroupBy> groups) {
        if (!groups.isEmpty()) {
            sql.append(GROUP_BY);
            for (int i = 0, size = groups.size(); i < size; i++) {
                buildGroupBy(groups.get(i));
                if (i < size - 1 && groups.get(i + 1).type == Type.GROUP_BY) {
                    sql.append(DELIMITER);
                }
            }
        }
    }

    protected void buildOrderBy(OrderBy order) {
        sql.append(warpKeyword(order.column)).append(order.desc ? DESC : EMPTY);
    }

    private void buildOrderBy(List<OrderBy> orders) {
        if (!orders.isEmpty()) {
            sql.append(ORDER_BY);
            for (int i = 0, size = orders.size(); i < size; i++) {
                buildOrderBy(orders.get(i));
                if (i < size - 1 && orders.get(i + 1).type == Type.ORDER_BY) {
                    sql.append(DELIMITER);
                }
            }
        }
    }
}