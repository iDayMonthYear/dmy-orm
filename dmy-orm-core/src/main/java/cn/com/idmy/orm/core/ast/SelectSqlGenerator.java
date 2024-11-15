package cn.com.idmy.orm.core.ast;

import cn.com.idmy.orm.core.ast.Node.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SelectSqlGenerator extends AbstractSqlGenerator {
    public static String gen(SelectChain<?> select) {
        List<Node> nodes = select.nodes();
        List<SelectField> selectFields = new ArrayList<>(nodes.size());
        List<Node> wheres = new ArrayList<>(nodes.size());
        List<GroupBy> groups = new ArrayList<>(1);
        List<OrderBy> orders = new ArrayList<>(4);
        Distinct distinct = null;
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
}