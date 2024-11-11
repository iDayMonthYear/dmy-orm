package cn.com.idmy.orm.core.ast;

import cn.com.idmy.orm.core.ast.Node.Cond;
import cn.com.idmy.orm.core.ast.Node.Or;
import cn.com.idmy.orm.test.User;
import cn.com.idmy.orm.test.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.lang.Console;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DeleteSqlGenerator extends AbstractSqlGenerator {
    public static String gen(DeleteChain<?> deleteChain) {
        List<Node> nodes = deleteChain.nodes();
        List<Node> wheres = new ArrayList<>(nodes.size());
        for (Node node : nodes) {
            if (node instanceof Cond) {
                wheres.add(node);
            } else if (node instanceof Or) {
                skipAdjoinOr(node, wheres);
            }
        }

        StringBuilder sql = new StringBuilder("delete from ").append(tableName(deleteChain.table()));
        buildWhere(wheres, sql);
        return sql.toString();
    }



    public static void main(String[] args) {
        UserDao dao = () -> User.class;
//        Console.log(Delete.of(dao).or().or().in(User::id, 1, 2, 3).like(User::name, "%dmy%").or());
        Console.log(DeleteChain.of(dao).or().or().eq(User::name, "1", "1".equals("2")).or().or().eq(User::createdAt, 1, true).or().or().eq(User::id, 1).or());
//        Console.log(Delete.of(dao).eq(User::id, 1).or().in(User::id, "1", "2", "3").like(User::name, "%dmy%"));
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