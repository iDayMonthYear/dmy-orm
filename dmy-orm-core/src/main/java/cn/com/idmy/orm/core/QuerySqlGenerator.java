package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.SqlNode.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static cn.com.idmy.orm.core.SqlConsts.*;

@Slf4j
class QuerySqlGenerator extends SqlGenerator {
    protected Query<?> query;

    protected QuerySqlGenerator(Query<?> query) {
        super(query.entityClass, query.nodes);
        this.query = query;
    }

    @Override
    protected Pair<String, List<Object>> doGenerate() {
        var selectColumns = new ArrayList<SqlSelectColumn>(1);
        var wheres = new ArrayList<SqlNode>(nodes.size());
        var groups = new ArrayList<SqlGroupBy>(1);
        var orders = new ArrayList<SqlOrderBy>(2);
        SqlDistinct distinct = null;
        for (int i = 0, size = nodes.size(); i < size; i++) {
            switch (nodes.get(i)) {
                case SqlCond cond -> wheres.add(cond);
                case SqlSelectColumn selectColumn -> selectColumns.add(selectColumn);
                case SqlGroupBy groupBy -> groups.add(groupBy);
                case SqlOrderBy orderBy -> orders.add(orderBy);
                case SqlOr or -> skipAdjoinOr(or, wheres);
                case SqlDistinct d -> distinct = d;
                case null, default -> {
                }
            }
        }

        sql.append(SELECT);
        params = new ArrayList<>(query.sqlParamsSize);

        if (distinct != null) {
            genDistinct(distinct);
            if (!selectColumns.isEmpty()) {
                sql.append(DELIMITER);
            }
        }

        genSelectColumn(selectColumns);
        sql.append(FROM).append(SqlConsts.STRESS_MARK).append(tableName).append(SqlConsts.STRESS_MARK);
        genWhere(wheres);
        genGroupBy(groups);
        genOrderBy(orders);
        if (query.limit != null) {
            sql.append(LIMIT).append(query.limit);
        }
        if (query.offset != null) {
            sql.append(OFFSET).append(query.offset);
        }
        return Pair.of(sql.toString(), params);
    }

    protected void genDistinct(SqlDistinct distinct) {
        var col = distinct.column;
        if (col == null) {
            sql.append(DISTINCT);
        } else {
            sql.append(DISTINCT).append(BRACKET_LEFT).append(warpKeyword(col)).append(BRACKET_RIGHT);
        }
    }

    protected String genSelectColumn(SqlSelectColumn sc) {
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

    protected void genSelectColumn(List<SqlSelectColumn> scs) {
        if (scs.isEmpty()) {
            sql.append(ASTERISK);
        } else {
            var set = new HashSet<>(scs.size());
            for (int i = 0, size = scs.size(); i < size; i++) {
                var sc = scs.get(i);
                var col = genSelectColumn(sc);
                if (set.contains(col)) {
                    throw new OrmException("select " + col + " 列名重复会导致映射到实体类异常");
                } else {
                    set.add(col);
                    if (i < size - 1 && scs.get(i + 1).type == SqlNodeType.SELECT_COLUMN) {
                        sql.append(DELIMITER);
                    }
                }
            }
        }
    }

    protected void genGroupBy(SqlGroupBy group) {
        sql.append(warpKeyword(group.column));
    }

    protected void genGroupBy(List<SqlGroupBy> groups) {
        if (!groups.isEmpty()) {
            sql.append(GROUP_BY);
            for (int i = 0, size = groups.size(); i < size; i++) {
                genGroupBy(groups.get(i));
                if (i < size - 1 && groups.get(i + 1).type == SqlNodeType.GROUP_BY) {
                    sql.append(DELIMITER);
                }
            }
        }
    }

    protected void genOrderBy(SqlOrderBy order) {
        sql.append(warpKeyword(order.column)).append(order.desc ? DESC : EMPTY);
    }

    private void genOrderBy(List<SqlOrderBy> orders) {
        if (!orders.isEmpty()) {
            sql.append(ORDER_BY);
            for (int i = 0, size = orders.size(); i < size; i++) {
                genOrderBy(orders.get(i));
                if (i < size - 1 && orders.get(i + 1).type == SqlNodeType.ORDER_BY) {
                    sql.append(DELIMITER);
                }
            }
        }
    }
}