package cn.com.idmy.orm.core.ast;

import cn.com.idmy.orm.core.ast.Node.*;
import cn.com.idmy.orm.test.User;
import cn.com.idmy.orm.test.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.lang.Console;

import java.util.ArrayList;
import java.util.List;

import static cn.com.idmy.orm.core.ast.SqlFn.ifNull;
import static cn.com.idmy.orm.core.ast.SqlFn.sum;

@Slf4j
public class SelectSqlGenerator extends AbstractSqlGenerator {
    public static String gen(SelectChain<?> select) {
        List<Node> nodes = select.nodes();
        List<SelectField> selectFields = new ArrayList<>(nodes.size());
        List<Node> wheres = new ArrayList<>(nodes.size());
        List<GroupBy> groups = new ArrayList<>(1);
        List<OrderBy> orders = new ArrayList<>(4);
        Distinct distinct = null;
        Having having = null;
        for (Node node : nodes) {
            switch (node) {
                case Cond cond -> wheres.add(cond);
                case SelectField selectField -> selectFields.add(selectField);
                case GroupBy groupBy -> groups.add(groupBy);
                case OrderBy orderBy -> orders.add(orderBy);
                case Or or -> skipAdjoinOr(or, wheres);
                case Distinct d -> {
                    distinct = d;
                }
                case Having h -> {
                    having = h;
                }
                case null, default -> {
                }
            }
        }

        StringBuilder sql = new StringBuilder("select ");
        if (distinct != null) {
            sql.append(builder(distinct));
            if (!selectFields.isEmpty()) {
                sql.append(", ");
            }
        }
        buildSelectField(selectFields, sql);
        sql.append(" from ").append(getTableName(select.table()));
        buildWhere(wheres, sql);
        buildGroupBy(groups, sql);
        if (having != null) {
            sql.append(" having ").append(having.expr());
        }
        buildOrderBy(orders, sql);
        return sql.toString();
    }

    private static void buildSelectField(List<SelectField> selectFields, StringBuilder sql) {
        if (selectFields.isEmpty()) {
            sql.append("*");
        } else {
            for (int i = 0, size = selectFields.size(); i < size; i++) {
                SelectField selectField = selectFields.get(i);
                sql.append(builder(selectField));
                if (i < size - 1) {
                    Type type = selectFields.get(i + 1).type();
                    if (type == Type.SELECT_FIELD) {
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
                sql.append(builder(group));
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
                sql.append(builder(order));
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
        Console.log(SelectChain.of(dao)
                .or()
                .or()
                .distinct(User::id)
                .select(SqlFn::count, "test")
                .select(() -> sum(User::id))
                .select(() -> ifNull(User::id, 1))
                .select(SqlFn::count)
                .select(User::createdAt, User::createdAt, User::createdAt, User::createdAt)
                .eq(User::createdAt, 1)
                .eq(User::createdAt, 1)
                .eq(User::createdAt, 1)
                .or()
                .eq(User::createdAt, 1)
                .or()
                .groupBy(User::createdAt, User::id)
                .orderBy(User::createdAt, true, User::id, true)
                .orderBy(User::name, true)
                .having("sum(id) > 1")
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