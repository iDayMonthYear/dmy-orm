package cn.com.idmy.orm.core.ast;

import cn.com.idmy.orm.core.ast.Node.*;
import cn.com.idmy.orm.test.User;
import cn.com.idmy.orm.test.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.lang.Console;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SelectSqlGenerator extends AbstractSqlGenerator {
    public static String gen(SelectChain<?> select) {
        List<Node> nodes = select.nodes();
        List<SelectField> selectFields = new ArrayList<>(nodes.size());
        List<Node> wheres = new ArrayList<>(nodes.size());
        List<GroupBy> groups = new ArrayList<>(nodes.size());
        List<OrderBy> orders = new ArrayList<>(4);
        for (Node node : nodes) {
            if (node instanceof Cond) {
                wheres.add(node);
            } else if (node instanceof SelectField sf) {
                selectFields.add(sf);
            } else if (node instanceof GroupBy group) {
                groups.add(group);
            } else if (node instanceof OrderBy order) {
                orders.add(order);
            } else if (node instanceof Or) {
                skipAdjoinOr(node, wheres);
            }
        }

        StringBuilder sql = new StringBuilder("select ");
        buildSelectFields(selectFields, sql);
        buildWhere(wheres, sql);
        buildGroupBy(groups, sql);
        buildOrderBy(orders, sql);
        return sql.toString();
    }

    private static void buildSelectFields(List<SelectField> sfs, StringBuilder sql) {
        if (!sfs.isEmpty()) {
            sql.append("  ");
            for (int i = 0, size = sfs.size(); i < size; i++) {
                SelectField selectField = sfs.get(i);
                sql.append(parseExpr(selectField));
                if (i < size - 1) {
                    Type type = sfs.get(i + 1).type();
                    if (type == Type.GROUP_BY) {
                        sql.append(", ");
                    }
                }
            }
        }
    }

    private static void buildGroupBy(List<GroupBy> groups, StringBuilder sql) {
        if (!groups.isEmpty()) {
            sql.append(" group by ");
            for (int i = 0, size = groups.size(); i < size; i++) {
                GroupBy group = groups.get(i);
                sql.append(parseExpr(group));
                if (i < size - 1) {
                    Type type = groups.get(i + 1).type();
                    if (type == Type.GROUP_BY) {
                        sql.append(", ");
                    }
                }
            }
        }
    }

    private static void buildOrderBy(List<OrderBy> orders, StringBuilder sql) {
        if (!orders.isEmpty()) {
            sql.append(" order by ");
            for (int i = 0, size = orders.size(); i < size; i++) {
                OrderBy order = orders.get(i);
                sql.append(parseExpr(order));
                if (i < size - 1) {
                    Type type = orders.get(i + 1).type();
                    if (type == Type.ORDER_BY) {
                        sql.append(", ");
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        UserDao dao = () -> User.class;
       /* Console.log(Update.of(dao)
                .or()
                .or()
                .like(User::name, "%dmy%")
                .eq(User::name, "1", "1".equals("0"))
                .eq(User::createdAt, 1)
                .or()
                .or()
                .set(User::username, "dmy")
                .set(User::username, "dmy")
                .set(User::username, "dmy")
                .in(User::id, 1, 2, 3)
                .like(User::name, "%dmy%")
                .or());*/
        Console.log(SelectChain.of(dao)
                .or()
                .or()
                .eq(User::createdAt, 1)
                .eq(User::createdAt, 1)
                .eq(User::createdAt, 1)
                .or()
                .eq(User::createdAt, 1)
                .or()
                .groupBy(User::createdAt, User::id)
                .orderBy(User::createdAt, true, User::id, true)
                .orderBy(User::name, true)
        );
    }
/*

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

    }*/
}