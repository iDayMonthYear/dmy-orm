package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.SqlNode.*;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.text.StrUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


@Slf4j
class QuerySqlGenerator extends SqlGenerator {
    @NotNull
    protected final Query<?> query;

    protected QuerySqlGenerator(@NotNull Query<?> q) {
        super(q.entityType, q.nodes);
        this.query = q;
    }

    @Override
    protected @NotNull Pair<String, List<Object>> doGen() {
        var selects = new ArrayList<SqlSelectColumn>(1);
        var wheres = new ArrayList<SqlNode>(nodes.size());
        var groups = new ArrayList<SqlGroupBy>(1);
        var orders = new ArrayList<SqlOrderBy>(2);
        SqlDistinct distinct = null;
        for (int i = 0, size = nodes.size(); i < size; i++) {
            switch (nodes.get(i)) {
                case SqlCond cond -> wheres.add(cond);
                case SqlSelectColumn select -> selects.add(select);
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
            if (!selects.isEmpty()) {
                sql.append(DELIMITER);
            }
        }

        genSelectColumn(selects);
        sql.append(FROM).append(STRESS_MARK).append(tableName).append(STRESS_MARK);
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

    protected void genDistinct(SqlDistinct d) {
        var col = d.column;
        if (StrUtil.isBlank(col)) {
            sql.append(DISTINCT);
        } else {
            sql.append(DISTINCT).append(BRACKET_LEFT).append(keyword(col)).append(BRACKET_RIGHT);
        }
    }

    protected String genSelectColumn(SqlSelectColumn sc) {
        var col = keyword(sc.column);
        if (sc.expr == null) {
            sql.append(col);
        } else {
            var expr = sc.expr;
            var fn = expr.get();
            var name = fn.name();
            if (name == SqlFnName.IF_NULL) {
                sql.append(name.getName()).append(BRACKET_LEFT).append(col).append(DELIMITER).append(PLACEHOLDER).append(BRACKET_RIGHT).append(BLANK).append(col);
                params.add(fn.value());
            } else if (name == SqlFnName.COUNT) {
                sql.append(name.getName()).append(BRACKET_LEFT).append(ASTERISK).append(BRACKET_RIGHT);
                if (!ASTERISK.equals(fn.column())) {
                    sql.append(BLANK).append(col);
                }
            } else {
                sql.append(name.getName()).append(BRACKET_LEFT).append(col).append(BRACKET_RIGHT).append(BLANK).append(col);
            }
        }
        return sc.column;
    }

    protected void genSelectColumn(List<SqlSelectColumn> ls) {
        if (ls.isEmpty()) {
            var table = Tables.getTable(entityType);
            var cols = table.columns();
            int len = cols.length;
            for (int i = 0; i < len; i++) {
                sql.append(keyword(cols[i].name()));
                if (i < len - 1) {
                    sql.append(DELIMITER);
                }
            }
        } else {
            var set = new HashSet<>(ls.size());
            for (int i = 0, size = ls.size(); i < size; i++) {
                var sc = ls.get(i);
                var col = genSelectColumn(sc);
                if (set.contains(col)) {
                    throw new OrmException("select {} 列名重复会导致映射到实体类异常", col);
                } else {
                    set.add(col);
                    if (i < size - 1 && ls.get(i + 1).type == SqlNodeType.SELECT_COLUMN) {
                        sql.append(DELIMITER);
                    }
                }
            }
        }
    }

    protected void genGroupBy(SqlGroupBy g) {
        sql.append(keyword(g.column));
    }

    protected void genGroupBy(List<SqlGroupBy> ls) {
        if (!ls.isEmpty()) {
            sql.append(GROUP_BY);
            for (int i = 0, size = ls.size(); i < size; i++) {
                genGroupBy(ls.get(i));
                if (i < size - 1 && ls.get(i + 1).type == SqlNodeType.GROUP_BY) {
                    sql.append(DELIMITER);
                }
            }
        }
    }

    protected void genOrderBy(SqlOrderBy o) {
        sql.append(keyword(o.column)).append(o.desc ? DESC : EMPTY);
    }

    private void genOrderBy(List<SqlOrderBy> ls) {
        if (!ls.isEmpty()) {
            sql.append(ORDER_BY);
            for (int i = 0, size = ls.size(); i < size; i++) {
                genOrderBy(ls.get(i));
                if (i < size - 1 && ls.get(i + 1).type == SqlNodeType.ORDER_BY) {
                    sql.append(DELIMITER);
                }
            }
        }
    }
}