package cn.com.idmy.orm.core;

import cn.com.idmy.orm.core.Node.*;
import cn.com.idmy.orm.util.SqlUtil;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.func.LambdaUtil;

import java.util.List;
import java.util.Objects;

import static cn.com.idmy.orm.core.SqlConsts.*;
import static cn.com.idmy.orm.core.SqlFnName.COUNT;


@Slf4j
public abstract class AbstractSqlGenerator {
    protected static String buildColumn(Object col) {
        if (col instanceof ColumnGetter<?, ?> getter) {
            return STRESS_MARK + LambdaUtil.getFieldName(getter) + STRESS_MARK;
        } else {
            SqlUtil.checkColumn((String) col);
            return STRESS_MARK + col + STRESS_MARK;
        }
    }

    private static String buildSqlExpr(String column, Object expr, List<Object> params) {
        StringBuilder sql = new StringBuilder();
        if (expr instanceof SqlOpExpr sqlOpExpr) {
            SqlOp sqlOp = sqlOpExpr.apply(new SqlOp(column));
            params.add(sqlOp.value());
            return sql.append(sqlOp.column()).append(BLANK).append(sqlOp.op()).append(BLANK).append(PLACEHOLDER).toString();
        } else {
            params.add(expr);
            placeholder(expr, sql);
        }
        return sql.toString();
    }

    private static void placeholder(Object value, StringBuilder sql) {
        if (value instanceof List<?> ls) {
            int size = ls.size();
            sql.append(BRACKET_LEFT);
            for (int i = 0; i < size; i++) {
                sql.append(PLACEHOLDER);
                if (i != size - 1) {
                    sql.append(DELIMITER);
                }
            }
            sql.append(BRACKET_RIGHT);
        } else if (value instanceof Object[] ls) {
            int size = ls.length;
            sql.append(BRACKET_LEFT);
            for (int i = 0; i < size; i++) {
                sql.append(PLACEHOLDER);
                if (i != size - 1) {
                    sql.append(DELIMITER);
                }
            }
            sql.append(BRACKET_RIGHT);
        } else {
            sql.append(PLACEHOLDER);
        }
    }

    private static StringBuilder buildCond(Cond cond, StringBuilder sql, List<Object> params) {
        String column = buildColumn(cond.column());
        String expr = buildSqlExpr(column, cond.expr(), params);
        return sql.append(column).append(BLANK).append(cond.op().getSymbol()).append(BLANK).append(expr);
    }

    private static StringBuilder buildSet(Set set, StringBuilder sql, List<Object> params) {
        var column = buildColumn(set.column());
        var expr = buildSqlExpr(column, set.expr(), params);
        return sql.append(column).append(BLANK).append(expr);
    }

    private static String buildSelectColumn(SelectColumn selectColumn, StringBuilder sql, List<Object> params) {
        var column = selectColumn.column();
        if (column instanceof ColumnGetter<?, ?> || column instanceof String) {
            var out = buildColumn(column);
            sql.append(out);
            return out;
        } else {
            var expr = (SqlFnExpr<?>) column;
            var fn = expr.apply();
            var name = fn.name();
            var col = (fn.column() == null && name == COUNT) ? ASTERISK : buildColumn(fn.column());
            var alias = selectColumn.alias() == null ? null : LambdaUtil.getFieldName(selectColumn.alias());
            var colOrAlias = Objects.requireNonNullElse(alias, col);
            if (name == SqlFnName.IF_NULL) {
                params.add(fn.value());
                sql.append(name.getName()).append(BRACKET_LEFT).append(col).append(DELIMITER).append(PLACEHOLDER).append(BRACKET_RIGHT).append(BLANK).append(colOrAlias);
            } else {
                sql.append(name.getName()).append(BRACKET_LEFT).append(col).append(BRACKET_RIGHT).append(BLANK).append(colOrAlias);
            }
            return colOrAlias;
        }
    }

    private static StringBuilder buildGroupBy(GroupBy group, StringBuilder sql) {
        sql.append(buildColumn(group.column()));
        return sql;
    }

    private static StringBuilder buildOrderBy(OrderBy order, StringBuilder sql) {
        Object column = order.column();
        String name;
        if (column instanceof ColumnGetter<?, ?> getter) {
            name = buildColumn(getter);
        } else {
            name = (String) column;
            //字符串类型可以是前端过来的。必须检查
            SqlUtil.checkColumn(name);
        }
        sql.append(name).append(order.desc() ? DESC : EMPTY);
        return sql;
    }

    private static StringBuilder buildDistinct(Distinct distinct, StringBuilder sql) {
        var column = distinct.column();
        if (column == null) {
            sql.append(DISTINCT);
        } else {
            sql.append(DISTINCT).append(BRACKET_LEFT).append(buildColumn(column)).append(BRACKET_RIGHT);
        }
        return sql;
    }

    @Nullable
    protected static Object builder(Node node, StringBuilder sql, List<Object> params) {
        return switch (node) {
            case Node.Or ignored -> sql.append(SqlConsts.OR);
            case Node.Cond cond -> buildCond(cond, sql, params);
            case Node.Set set -> buildSet(set, sql, params);
            case Node.GroupBy group -> buildGroupBy(group, sql);
            case Node.OrderBy order -> buildOrderBy(order, sql);
            case Node.SelectColumn col -> buildSelectColumn(col, sql, params);
            case Node.Distinct distinct -> buildDistinct(distinct, sql);
            case null, default -> null;
        };
    }

    protected static void skipAdjoinOr(Node node, List<Node> wheres) {
        if (CollUtil.isNotEmpty(wheres)) {
            if (wheres.getLast().type() == Type.OR) {
                if (log.isDebugEnabled()) {
                    log.warn("存在相邻的or，已自动移除");
                }
            } else {
                wheres.add(node);
            }
        }
    }

    private static void removeLastOr(List<Node> wheres) {
        if (CollUtil.isNotEmpty(wheres) && wheres.getLast() instanceof Or) {
            wheres.removeLast();
            if (log.isDebugEnabled()) {
                log.warn("where条件最后存在or，已自动移除");
            }
        }
    }

    protected static void buildWhere(List<Node> wheres, StringBuilder sql, List<Object> params) {
        if (!wheres.isEmpty()) {
            removeLastOr(wheres);
            sql.append(WHERE);
            for (int i = 0, size = wheres.size(); i < size; i++) {
                Node node = wheres.get(i);
                builder(node, sql, params);
                if (i < size - 1) {
                    Type type = wheres.get(i + 1).type();
                    if (type == Type.COND && node.type() != Type.OR) {
                        sql.append(AND);
                    }
                }
            }
        }
    }
}
