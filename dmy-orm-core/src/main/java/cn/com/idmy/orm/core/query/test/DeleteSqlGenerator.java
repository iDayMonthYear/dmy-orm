package cn.com.idmy.orm.core.query.test;

import cn.com.idmy.orm.annotation.Table;
import cn.com.idmy.orm.annotation.TableField;
import cn.com.idmy.orm.core.query.ast.*;
import cn.com.idmy.orm.core.query.fn.SqlExpressionFn;
import cn.com.idmy.orm.core.util.LambdaUtil;
import org.dromara.hutool.core.lang.Console;
import org.dromara.hutool.core.text.StrUtil;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

public class DeleteSqlGenerator {

    private <T> String generate(Delete<T> delete) {
        StringBuilder sql = new StringBuilder();
        List<Object> asts = delete.root().asts();
        for (Object node : asts) {
            if (node instanceof From<?, ?> from) {
                String tableName = tableName(from.table());
                sql.append("delete from ").append(tableName);
            } else if (node instanceof Where) {
                sql.append(" where ");
            } else if (node instanceof And) {
                sql.append(" and ");
            } else if (node instanceof Or) {
                sql.append(" or ");
            } else if (node instanceof Condition<?, ?> condition) {
                String field = fieldName(condition.field());
                sql.append(field).append(" ").append(condition.op().getSymbol()).append(" ");
                Object value = condition.expr();
                if (value instanceof SqlExpression expr) {
                    SqlExpressionFn fn = expr.apply(new SqlExpressionFn(field));
                    sql.append(fn.expr());
                } else {
                    sql.append(formatValue(value));
                }
            }
        }
        Console.log(sql);
        return sql.toString();
    }

    private <T> String generate(Update<T> update) {
        StringBuilder sql = new StringBuilder("update ").append(tableName(update.table())).append(" ");
        List<Object> asts = update.root().asts();
        for (int i = 0, astsSize = asts.size(); i < astsSize; i++) {
            Object node = asts.get(i);
            if (node instanceof Where) {
                sql.append(" where ");
            } else if (node instanceof And) {
                sql.append(" and ");
            } else if (node instanceof Or) {
                sql.append(" or ");
            } else if (node instanceof Condition<?, ?> condition) {
                String field = fieldName(condition.field());
                sql.append(field).append(" ").append(condition.op().getSymbol()).append(" ");
                Object value = condition.expr();
                if (value instanceof SqlExpression expr) {
                    SqlExpressionFn fn = expr.apply(new SqlExpressionFn(field));
                    sql.append(fn.expr());
                } else {
                    sql.append(formatValue(value));
                }
            } else if (node instanceof Set<?, ?> set) {
                String field = fieldName(set.field());
                sql.append(field).append(" = ");
                Object value = set.expr();
                if (value instanceof SqlExpression expr) {
                    SqlExpressionFn fn = expr.apply(new SqlExpressionFn(field));
                    sql.append(fn.expr());
                } else {
                    sql.append(formatValue(value));
                }
                Object next = asts.get(i + 1);
                if (next instanceof Set) {
                    sql.append(", ");
                }
            }
        }
        Console.log(sql);
        return sql.toString();
    }

    private String tableName(Class<?> entityClass) {
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table table = entityClass.getAnnotation(Table.class);
            String value = table.value();
            return StrUtil.isBlank(value) ? entityClass.getSimpleName() : value;
        } else {
            return entityClass.getSimpleName();
        }
    }

    private String fieldName(Field field) {
        if (field.isAnnotationPresent(TableField.class)) {
            TableField tableField = field.getAnnotation(TableField.class);
            String value = tableField.value();
            return StrUtil.isBlank(value) ? field.getName() : value;
        } else {
            return field.getName(); // 默认使用字段名
        }
    }

    private String fieldName(Object field) {
        if (field instanceof FieldGetter<?, ?> getter) {
            return LambdaUtil.fieldName(getter);
        } else {
            return (String) field;
        }
    }

    private String formatValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String str) {
            return "'" + str.replace("'", "''") + "'";
        }
        return value.toString();
    }

    public static void main(String[] args) {
        UserDao dao = () -> User.class;

        new DeleteSqlGenerator().generate(Delete.of(dao).from()
                .where()
                .eq(User::id, c -> c.plus(1))  // 使用函数构建表达式
                .and()
                .eq("name", "test")  // 普通字符串值
                .or()
                .eq(User::username, "test")  // 普通字符串值
                .semi());

        new DeleteSqlGenerator().generate(Update.of(dao)
                .set(User::id, 1)
                .set(User::username, "dmy")
                .set(User::name, f -> f.plus(1))
                .where()
                .eq(User::createdAt, LocalDateTime.now())  // 使用函数构建表达式
                .and()
                .eq("name", "test")  // 普通字符串值
                .or()
                .eq(User::username, "test")  // 普通字符串值
                .semi());

    }
}