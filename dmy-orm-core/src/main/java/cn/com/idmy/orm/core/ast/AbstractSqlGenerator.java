package cn.com.idmy.orm.core.ast;

import cn.com.idmy.orm.annotation.Table;
import cn.com.idmy.orm.annotation.TableField;
import cn.com.idmy.orm.core.ast.Node.*;
import cn.com.idmy.orm.core.util.LambdaUtil;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.text.StrUtil;

import java.lang.reflect.Field;
import java.util.List;

@Slf4j
public abstract class AbstractSqlGenerator {
    protected static String tableName(Class<?> entityClass) {
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table table = entityClass.getAnnotation(Table.class);
            String value = table.value();
            return StrUtil.isBlank(value) ? entityClass.getSimpleName() : value;
        } else {
            return entityClass.getSimpleName();
        }
    }

    protected static String fieldName(Field field) {
        if (field.isAnnotationPresent(TableField.class)) {
            TableField tableField = field.getAnnotation(TableField.class);
            String value = tableField.value();
            return StrUtil.isBlank(value) ? field.getName() : value;
        } else {
            return field.getName(); // 默认使用字段名
        }
    }

    protected static String field(Node.Field field) {
        if (field.name() instanceof FieldGetter<?, ?> getter) {
            field.value(LambdaUtil.fieldName(getter));
        } else {
            field.value(field.name());
        }
        return (String) field.value();
    }

    private static Object parseSqlExpr(String field, Object expr) {
        if (expr instanceof SqlExpr sqlExpr) {
            SqlExprFn fn = new SqlExprFn(field);
            SqlExprFn apply = sqlExpr.apply(fn);
            return apply.expr();
        } else {
            return formatValue(expr);
        }
    }

    private static String parseCond(Cond cond) {
        field(cond.field());
        Object val = parseSqlExpr((String) cond.field().value(), cond.expr());
        cond.value(val);
        return cond.field().value() + " " + cond.op().getSymbol() + " " + val;
    }

    private static String parseSet(Set set) {
        field(set.field());
        Object val = parseSqlExpr((String) set.field().value(), set.expr());
        set.value(val);
        return set.field().value() + " = " + val;
    }

    private static String parseSelectField(SelectField selectField) {
        field(selectField.field());
        return (String) selectField.field().value();
    }

    private static String parseGroupBy(GroupBy group) {
        field(group.field());
        return (String) group.field().value();
    }

    private static String parseOrderBy(OrderBy order) {
        field(order.field());
        return StrUtil.format("{} {}", order.field().value(), order.desc() ? "desc" : "");
    }

    protected static Object parseExpr(Node node) {
        return switch (node) {
            case Node.Field field -> field(field);
            case Node.Or ignored -> " or ";
            case Node.Cond cond -> parseCond(cond);
            case Node.Set set -> parseSet(set);
            case Node.GroupBy group -> parseGroupBy(group);
            case Node.OrderBy order -> parseOrderBy(order);
            case Node.SelectField sf -> parseSelectField(sf);
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
                sql.append(parseExpr(node));
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
