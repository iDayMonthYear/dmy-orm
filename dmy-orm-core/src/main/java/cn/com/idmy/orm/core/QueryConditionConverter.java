package cn.com.idmy.orm.core;

import cn.com.idmy.orm.core.SqlNode.*;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * 查询条件转换器，用于将链式查询条件转换为可在MyBatis XML中使用的字符串
 */
@Slf4j
@UtilityClass
public class QueryConditionConverter {

    /**
     * 将查询对象转换为条件字符串，可在MyBatis XML中使用
     *
     * @param query 查询对象
     * @return 条件字符串
     */
    public String toConditionString(@NotNull Query<?, ?> query) {
        var wheres = new ArrayList<SqlNode>(query.nodes.size());
        for (int i = 0, size = query.nodes.size(); i < size; i++) {
            switch (query.nodes.get(i)) {
                case SqlCond cond -> wheres.add(cond);
                case SqlOr or -> SqlGenerator.skipAdjoinOr(or, wheres);
                default -> {
                }
            }
        }

        if (wheres.isEmpty()) {
            return "";
        }

        SqlGenerator.removeLastOr(wheres);
        StringBuilder sql = new StringBuilder();
        for (int i = 0, size = wheres.size(); i < size; i++) {
            var node = wheres.get(i);
            genCondOr(sql, node);
            if (i < size - 1) {
                if (wheres.get(i + 1).type != Type.OR && node.type != Type.OR) {
                    sql.append(SqlGenerator.AND);
                }
            }
        }
        return sql.toString();
    }

    /**
     * 将查询对象转换为排序字符串，可在MyBatis XML中使用
     *
     * @param query 查询对象
     * @return 排序字符串
     */
    public String toOrderByString(@NotNull Query<?, ?> query) {
        var orders = new ArrayList<SqlOrderBy>(query.nodes.size());
        for (int i = 0, size = query.nodes.size(); i < size; i++) {
            if (query.nodes.get(i) instanceof SqlOrderBy orderBy) {
                orders.add(orderBy);
            }
        }

        if (orders.isEmpty()) {
            return "";
        }

        StringBuilder sql = new StringBuilder();
        for (int i = 0, size = orders.size(); i < size; i++) {
            var orderBy = orders.get(i);
            sql.append(keyword(orderBy.column())).append(orderBy.desc() ? SqlGenerator.DESC : "");
            if (i < size - 1) {
                sql.append(SqlGenerator.DELIMITER);
            }
        }
        return sql.toString();
    }

    /**
     * 将查询对象转换为分组字符串，可在MyBatis XML中使用
     *
     * @param query 查询对象
     * @return 分组字符串
     */
    public String toGroupByString(@NotNull Query<?, ?> query) {
        var groups = new ArrayList<SqlGroupBy>(query.nodes.size());
        for (int i = 0, size = query.nodes.size(); i < size; i++) {
            if (query.nodes.get(i) instanceof SqlGroupBy groupBy) {
                groups.add(groupBy);
            }
        }

        if (groups.isEmpty()) {
            return "";
        }

        StringBuilder sql = new StringBuilder();
        for (int i = 0, size = groups.size(); i < size; i++) {
            var groupBy = groups.get(i);
            sql.append(keyword(groupBy.column()));
            if (i < size - 1) {
                sql.append(SqlGenerator.DELIMITER);
            }
        }
        return sql.toString();
    }

    private void genCondOr(@NotNull StringBuilder sql, @NotNull SqlNode node) {
        if (node instanceof SqlOr) {
            sql.append(SqlGenerator.OR);
        } else if (node instanceof SqlCond cond) {
            genCond(sql, cond);
        }
    }

    private void genCond(@NotNull StringBuilder sql, @NotNull SqlCond cond) {
        var col = cond.column();
        sql.append(keyword(col)).append(SqlGenerator.BLANK).append(cond.op().getSymbol()).append(SqlGenerator.BLANK);

        Object expr = cond.expr();
        if (expr instanceof SqlOpExpr) {
            sql.append(keyword(col));
        } else {
            switch (cond.op()) {
                case IS_NULL, IS_NOT_NULL -> {
                    // 不需要添加占位符
                }
                case BETWEEN, NOT_BETWEEN -> sql.append("? and ?");
                default -> {
                    if (expr instanceof Object[] || expr instanceof java.util.Collection<?>) {
                        sql.append(genPlaceholders(expr));
                    } else {
                        sql.append("?");
                    }
                }
            }
        }
    }

    private String genPlaceholders(Object val) {
        StringBuilder ph = new StringBuilder();
        if (val instanceof java.util.Collection<?> ls) {
            genPlaceholder(ph, ls.size());
        } else if (val instanceof Object[] arr) {
            genPlaceholder(ph, arr.length);
        } else {
            ph.append("?");
        }
        return ph.toString();
    }

    private void genPlaceholder(@NotNull StringBuilder ph, int size) {
        ph.append(SqlGenerator.BRACKET_LEFT);
        for (int i = 0; i < size; i++) {
            ph.append("?");
            if (i != size - 1) {
                ph.append(SqlGenerator.DELIMITER);
            }
        }
        ph.append(SqlGenerator.BRACKET_RIGHT);
    }

    private String keyword(@NotNull String val) {
        return SqlGenerator.STRESS_MARK + val + SqlGenerator.STRESS_MARK;
    }
} 