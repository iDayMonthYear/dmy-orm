package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.base.util.Assert;
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
    protected final @NotNull Query<?> query;

    protected QuerySqlGenerator(@NotNull Query<?> q) {
        super(q.entityType, q.nodes);
        this.query = q;
    }

    @Override
    protected @NotNull Pair<String, List<Object>> doGenerate() {
        if (!query.hasCond && !query.force) {
            if (query.limit == null && query.offset == null) {
                if (!query.hasAggregate) {
                    throw new OrmException("查询语句没有条件！使用 force() 强制查询全部数据");
                }
            }
        }
        var selects = new ArrayList<SelectSqlColumn>(1);
        var wheres = new ArrayList<SqlNode>(nodes.size());
        var groups = new ArrayList<SqlGroupBy>(1);
        var orders = new ArrayList<SqlOrderBy>(2);
        SqlDistinct distinct = null;
        for (int i = 0, size = nodes.size(); i < size; i++) {
            switch (nodes.get(i)) {
                case SqlCond cond -> wheres.add(cond);
                case SelectSqlColumn select -> selects.add(select);
                case SqlGroupBy groupBy -> groups.add(groupBy);
                case SqlOrderBy orderBy -> orders.add(orderBy);
                case SqlOr or -> skipAdjoinOr(or, wheres);
                case SqlDistinct d -> distinct = d;
                case null, default -> {
                }
            }
        }

        sql.append(SELECT);
        values = new ArrayList<>(query.sqlParamsSize);

        if (distinct != null) {
            genDistinct(distinct);
            if (!selects.isEmpty()) {
                sql.append(DELIMITER);
            }
        }

        genSelectColumn(selects);
        sql.append(FROM).append(tableInfo.schema()).append(STRESS_MARK).append(tableInfo.name()).append(STRESS_MARK);
        genWhere(wheres);
        genGroupBy(groups);
        genOrderBy(orders);
        if (query.limit != null) {
            sql.append(LIMIT).append(query.limit);
        }
        if (query.offset != null) {
            Assert.notNull(query.limit, "offset 必须与 limit 一起使用");
            sql.append(OFFSET).append(query.offset);
        }
        return new Pair<>(sql.toString(), values);
    }

    protected void genDistinct(SqlDistinct d) {
        var col = d.column;
        if (StrUtil.isBlank(col)) {
            sql.append(DISTINCT);
        } else {
            sql.append(DISTINCT).append(BRACKET_LEFT).append(keyword(col)).append(BRACKET_RIGHT);
        }
    }

    protected String genSelectColumn(SelectSqlColumn sc) {
        var col = keyword(sc.column);
        if (sc.expr == null) {
            sql.append(col);
        } else {
            var expr = sc.expr;
            var fn = expr.get();
            var name = fn.name();
            if (name == SqlFnName.IF_NULL) {
                sql.append(name.getName()).append(BRACKET_LEFT).append(col).append(DELIMITER).append(PLACEHOLDER).append(BRACKET_RIGHT).append(BLANK).append(col);
                values.add(fn.value());
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

    protected void genSelectColumn(List<SelectSqlColumn> ls) {
        if (ls.isEmpty()) {
            sql.append(ASTERISK);
        } else {
            var set = new HashSet<>(ls.size());
            for (int i = 0, size = ls.size(); i < size; i++) {
                var sc = ls.get(i);
                var col = genSelectColumn(sc);
                if (set.contains(col)) {
                    throw new OrmException("select {} 列名重复会导致映射到实体类异常", col);
                } else {
                    set.add(col);
                    if (i < size - 1 && ls.get(i + 1).type == Type.SELECT_COLUMN) {
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
                if (i < size - 1 && ls.get(i + 1).type == Type.GROUP_BY) {
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
                if (i < size - 1 && ls.get(i + 1).type == Type.ORDER_BY) {
                    sql.append(DELIMITER);
                }
            }
        }
    }
}