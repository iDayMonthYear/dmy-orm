package cn.com.idmy.orm.core.ast;

import cn.com.idmy.orm.annotation.Table;
import cn.com.idmy.orm.annotation.TableField;
import cn.com.idmy.orm.core.ast.Node.*;
import cn.com.idmy.orm.core.util.LambdaUtil;
import cn.com.idmy.orm.core.util.SqlUtil;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.text.StrUtil;

import java.lang.reflect.Field;
import java.util.List;

@Slf4j
public abstract class AbstractSqlGenerator {
    protected static String getTableName(Class<?> entityClass) {
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table table = entityClass.getAnnotation(Table.class);
            String value = table.value();
            return StrUtil.isBlank(value) ? entityClass.getSimpleName() : value;
        } else {
            return entityClass.getSimpleName();
        }
    }

    protected static String getFieldName(Field field) {
        if (field.isAnnotationPresent(TableField.class)) {
            TableField tableField = field.getAnnotation(TableField.class);
            String value = tableField.value();
            return StrUtil.isBlank(value) ? field.getName() : value;
        } else {
            return field.getName(); // 默认使用字段名
        }
    }

    protected static String getField(Node.Field field) {
        if (field.name() instanceof FieldGetter<?, ?> getter) {
            field.value(LambdaUtil.fieldName(getter));
        } else {
            field.value(field.name());
        }
        String f = (String) field.value();
        SqlUtil.checkField(f);
        return f;
    }

    private static Object parseSqlExpr(String field, Object expr) {
        if (expr instanceof SqlOpExpr sqlOpExpr) {
            SqlOp fn = new SqlOp(field);
            SqlOp apply = sqlOpExpr.apply(fn);
            return apply.expr();
        } else {
            return formatValue(expr);
        }
    }

    private static String buildCond(Cond cond) {
        getField(cond.field());
        Object val = parseSqlExpr((String) cond.field().value(), cond.expr());
        cond.value(val);
        return cond.field().value() + " " + cond.op().getSymbol() + " " + val;
    }

    private static String buildSet(Set set) {
        getField(set.field());
        Object val = parseSqlExpr((String) set.field().value(), set.expr());
        set.value(val);
        return set.field().value() + " = " + val;
    }

    private static String buildSelectField(SelectField selectField) {
        Object value = selectField.field();
        if (value instanceof Node.Field f) {
            return getField(f);
        } else if (value instanceof SqlFnExpr<?> exp) {
            SqlFn<?> fn = exp.apply();
            SqlFnName name = fn.name();
            String field = getField(fn.field());
            String alias = selectField.alias();
            if (name == SqlFnName.IF_NULL) {
                return StrUtil.format("{}({}, {}) {}", name.getName(), field, fn.expr(), alias == null ? field : alias);
            } else {
                return StrUtil.format("{}({}) {}", name.getName(), field, alias == null ? field : alias);
            }
        } else {
            return (String) value;
        }
    }

    private static String buildGroupBy(GroupBy group) {
        getField(group.field());
        return (String) group.field().value();
    }

    private static String buildOrderBy(OrderBy order) {
        getField(order.field());
        return StrUtil.format("{} {}", order.field().value(), order.desc() ? "desc" : "");
    }

    private static String buildDistinct(Distinct distinct) {
        Node.Field field = distinct.field();
        if (field == null) {
            return "distinct ";
        } else {
            String str = getField(field);
            return StrUtil.format("distinct({}) ", str);
        }
    }

    protected static Object builder(Node node) {
        return switch (node) {
            case Node.Field field -> getField(field);
            case Node.Or ignored -> " or ";
            case Node.Cond cond -> buildCond(cond);
            case Node.Set set -> buildSet(set);
            case Node.GroupBy group -> buildGroupBy(group);
            case Node.OrderBy order -> buildOrderBy(order);
            case Node.SelectField sf -> buildSelectField(sf);
            case Node.Distinct distinct -> buildDistinct(distinct);
            case null, default -> {
                yield null;
            }
        };
    }

    protected static String formatValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String str) {
            return "'" + str.replace("'", "''") + "'";
        }
        if (value instanceof Number) {
            return value.toString();
        }
        if (value instanceof List) {
            return StrUtil.join(",", value);
        }
        if (value instanceof Object[]) {
            return "(" + StrUtil.join(",", value) + ")";
        }
        return value.toString();
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

    protected static void buildWhere(List<Node> wheres, StringBuilder sql) {
        if (!wheres.isEmpty()) {
            removeLastOr(wheres);
            sql.append(" where ");
            for (int i = 0, whereNodesSize = wheres.size(); i < whereNodesSize; i++) {
                Node node = wheres.get(i);
                sql.append(builder(node));
                if (i < whereNodesSize - 1) {
                    Type type = wheres.get(i + 1).type();
                    if (type == Type.COND && node.type() != Type.OR) {
                        sql.append(" and ");
                    }
                }
            }
        }
    }
}
