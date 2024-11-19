package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.Node.*;
import cn.com.idmy.orm.util.OrmUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cn.com.idmy.orm.core.SqlConsts.ASTERISK;
import static cn.com.idmy.orm.core.SqlConsts.DELIMITER;
import static cn.com.idmy.orm.core.SqlConsts.FROM;
import static cn.com.idmy.orm.core.SqlConsts.GROUP_BY;
import static cn.com.idmy.orm.core.SqlConsts.ORDER_BY;
import static cn.com.idmy.orm.core.SqlConsts.SELECT;

@Slf4j
public class SelectSqlGenerator extends AbstractSqlGenerator {
    public static Pair<String, List<Object>> gen(SelectChain<?> select) {
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
                case Distinct d -> distinct = d;
                case null, default -> {
                }
            }
        }

        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT);
        if (distinct != null) {
            builder(distinct, sql, params);
            if (!selectFields.isEmpty()) {
                sql.append(DELIMITER);
            }
        }
        buildSelectField(selectFields, sql, params);
        sql.append(FROM).append(OrmUtil.getTableName(select.entityClass()));
        buildWhere(wheres, sql, params);
        buildGroupBy(groups, sql, params);
        buildOrderBy(orders, sql, params);
        return Pair.of(sql.toString(), params);
    }

    private static void buildSelectField(List<SelectField> selectFields, StringBuilder sql, List<Object> params) {
        if (selectFields.isEmpty()) {
            sql.append(ASTERISK);
        } else {
            Set<String> set = new HashSet<>(selectFields.size());
            for (int i = 0, size = selectFields.size(); i < size; i++) {
                SelectField selectField = selectFields.get(i);
                String field = (String) builder(selectField, sql, params);
                if (log.isDebugEnabled()) {
                    if (set.contains(field)) {
                        log.error("select {} 字段名重复会导致映射到实体类异常", field);
                    }
                    set.add(field);
                }
                if (i < size - 1) {
                    Type type = selectFields.get(i + 1).type();
                    if (type == Type.SELECT_FIELD) {
                        sql.append(DELIMITER);
                    }
                }
            }
        }
    }

    private static void buildGroupBy(List<GroupBy> groups, StringBuilder sql, List<Object> params) {
        if (!groups.isEmpty()) {
            sql.append(GROUP_BY);
            for (int i = 0, size = groups.size(); i < size; i++) {
                GroupBy group = groups.get(i);
                builder(group, sql, params);
                if (i < size - 1) {
                    Type type = groups.get(i + 1).type();
                    if (type == Type.GROUP_BY) {
                        sql.append(DELIMITER);
                    }
                }
            }
        }
    }

    private static void buildOrderBy(List<OrderBy> orders, StringBuilder sql, List<Object> params) {
        if (!orders.isEmpty()) {
            sql.append(ORDER_BY);
            for (int i = 0, size = orders.size(); i < size; i++) {
                OrderBy order = orders.get(i);
                builder(order, sql, params);
                if (i < size - 1) {
                    Type type = orders.get(i + 1).type();
                    if (type == Type.ORDER_BY) {
                        sql.append(DELIMITER);
                    }
                }
            }
        }
    }
}