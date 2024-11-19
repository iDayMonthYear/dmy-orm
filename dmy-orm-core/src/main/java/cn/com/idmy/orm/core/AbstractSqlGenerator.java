package cn.com.idmy.orm.core;

import cn.com.idmy.orm.core.Node.*;
import cn.com.idmy.orm.util.LambdaUtil;
import cn.com.idmy.orm.util.SqlUtil;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.collection.CollUtil;

import java.util.List;
import java.util.Objects;

import static cn.com.idmy.orm.core.SqlConsts.*;
import static cn.com.idmy.orm.core.SqlFnName.COUNT;


@Slf4j
public abstract class AbstractSqlGenerator {
    protected static String buildField(Object field) {
        if (field instanceof FieldGetter<?, ?> f) {
            return "`" + LambdaUtil.fieldName(f) + "`";
        } else {
            SqlUtil.checkField((String) field);
            return "`" + field + "`";
        }
    }

    private static String parseSqlExpr(String field, Object expr, List<Object> params) {
        StringBuilder sql = new StringBuilder();
        if (expr instanceof SqlOpExpr sqlOpExpr) {
            SqlOp sqlOp = sqlOpExpr.apply(new SqlOp(field));
            params.add(sqlOp.value());
            return sql.append(sqlOp.field()).append(BLANK).append(sqlOp.op()).append(BLANK).append(PLACEHOLDER).toString();
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
        String field = buildField(cond.field());
        String expr = parseSqlExpr(field, cond.expr(), params);
        sql.append(field).append(BLANK).append(cond.op().getSymbol()).append(BLANK).append(expr);
        return sql;
    }

    private static StringBuilder buildSet(Set set, StringBuilder sql, List<Object> params) {
        String field = buildField(set.field());
        String expr = parseSqlExpr(field, set.expr(), params);
        sql.append(field).append(BLANK).append(expr);
        return sql;
    }

    private static String buildSelectField(SelectField selectField, StringBuilder sql, List<Object> params) {
        Object value = selectField.field();
        if (value instanceof FieldGetter<?, ?>  || value instanceof String) {
            String out = buildField(value);
            sql.append(out);
            return out;
        } else if (value instanceof SqlFnExpr<?> exp) {
            SqlFn<?> fn = exp.apply();
            SqlFnName name = fn.name();
            String field = (fn.field() == null && name == COUNT) ? ASTERISK : buildField(fn.field());
            String alias = selectField.alias() == null ? null : LambdaUtil.fieldName(selectField.alias());
            String fieldOrAlias = Objects.requireNonNullElse(alias, field);
            if (name == SqlFnName.IF_NULL) {
                params.add(fn.value());
                sql.append(name.getName()).append(BRACKET_LEFT).append(field).append(DELIMITER).append(PLACEHOLDER).append(BRACKET_RIGHT).append(BLANK).append(fieldOrAlias);
            } else {
                sql.append(name.getName()).append(BRACKET_LEFT).append(field).append(BRACKET_RIGHT).append(BLANK).append(fieldOrAlias);
            }
            return fieldOrAlias;
        } else {
            sql.append(value);
            return (String) value;
        }
    }

    private static StringBuilder buildGroupBy(GroupBy group, StringBuilder sql) {
        sql.append(buildField(group.field()));
        return sql;
    }

    private static StringBuilder buildOrderBy(OrderBy order, StringBuilder sql) {
        Object field = order.field();
        String name;
        if (field instanceof FieldGetter<?, ?> getter) {
            name = buildField(getter);
        } else {
            name = (String) field;
            //字符串类型可以是前端过来的。必须检查
            SqlUtil.checkField(name);
        }
        sql.append(name).append(order.desc() ? DESC : EMPTY);
        return sql;
    }

    private static StringBuilder buildDistinct(Distinct distinct, StringBuilder sql) {
        FieldGetter<?, ?> field = distinct.field();
        if (field == null) {
            sql.append(DISTINCT);
        } else {
            sql.append(DISTINCT).append(BRACKET_LEFT).append(buildField(field)).append(BRACKET_RIGHT);
        }
        return sql;
    }

    protected static Object builder(Node node, StringBuilder sql, List<Object> params) {
        return switch (node) {
            case Node.Or ignored -> sql.append(SqlConsts.OR);
            case Node.Cond cond -> buildCond(cond, sql, params);
            case Node.Set set -> buildSet(set, sql, params);
            case Node.GroupBy group -> buildGroupBy(group, sql);
            case Node.OrderBy order -> buildOrderBy(order, sql);
            case Node.SelectField sf -> buildSelectField(sf, sql, params);
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
