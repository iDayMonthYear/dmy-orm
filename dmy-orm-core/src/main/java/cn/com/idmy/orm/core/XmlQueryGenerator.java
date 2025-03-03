package cn.com.idmy.orm.core;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.text.StrUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * XML查询生成器，用于生成XML查询所需的条件、排序和分组字符串
 */
@Slf4j
public class XmlQueryGenerator extends QuerySqlGenerator {
    /**
     * 条件字符串
     */
    private String conditionString;

    /**
     * 排序字符串
     */
    private String orderByString;

    /**
     * 分组字符串
     */
    private String groupByString;

    /**
     * 创建XML查询生成器
     *
     * @param query 查询对象
     */
    protected XmlQueryGenerator(@NotNull Query<?, ?> query) {
        super(query);
        generateQueryStrings();
    }

    /**
     * 生成查询字符串
     */
    private void generateQueryStrings() {
        // 收集条件节点
        var wheres = new ArrayList<SqlNode>(nodes.size());
        for (int i = 0, size = nodes.size(); i < size; i++) {
            if (nodes.get(i) instanceof SqlNode.SqlCond cond) {
                wheres.add(cond);
            } else if (nodes.get(i) instanceof SqlNode.SqlOr or) {
                skipAdjoinOr(or, wheres);
            }
        }

        // 生成条件字符串
        if (!wheres.isEmpty()) {
            StringBuilder condSql = new StringBuilder();
            removeLastOr(wheres);
            for (int i = 0, size = wheres.size(); i < size; i++) {
                var node = wheres.get(i);
                genCondOrForXml(condSql, node);
                if (i < size - 1) {
                    if (wheres.get(i + 1).type != SqlNode.Type.OR && node.type != SqlNode.Type.OR) {
                        condSql.append(AND);
                    }
                }
            }
            this.conditionString = condSql.toString();
        }

        // 收集排序节点
        var orders = new ArrayList<SqlNode.SqlOrderBy>(nodes.size());
        for (int i = 0, size = nodes.size(); i < size; i++) {
            if (nodes.get(i) instanceof SqlNode.SqlOrderBy orderBy) {
                orders.add(orderBy);
            }
        }

        // 生成排序字符串
        if (!orders.isEmpty()) {
            StringBuilder orderSql = new StringBuilder();
            for (int i = 0, size = orders.size(); i < size; i++) {
                var orderBy = orders.get(i);
                orderSql.append(keyword(orderBy.column)).append(orderBy.desc ? DESC : EMPTY);
                if (i < size - 1) {
                    orderSql.append(DELIMITER);
                }
            }
            this.orderByString = orderSql.toString();
        }

        // 收集分组节点
        var groups = new ArrayList<SqlNode.SqlGroupBy>(nodes.size());
        for (int i = 0, size = nodes.size(); i < size; i++) {
            if (nodes.get(i) instanceof SqlNode.SqlGroupBy groupBy) {
                groups.add(groupBy);
            }
        }

        // 生成分组字符串
        if (!groups.isEmpty()) {
            StringBuilder groupSql = new StringBuilder();
            for (int i = 0, size = groups.size(); i < size; i++) {
                var groupBy = groups.get(i);
                groupSql.append(keyword(groupBy.column));
                if (i < size - 1) {
                    groupSql.append(DELIMITER);
                }
            }
            this.groupByString = groupSql.toString();
        }
    }

    /**
     * 生成XML条件OR语句
     */
    private void genCondOrForXml(@NotNull StringBuilder sql, @NotNull SqlNode node) {
        if (node instanceof SqlNode.SqlOr) {
            sql.append(OR);
        } else if (node instanceof SqlNode.SqlCond cond) {
            genCondForXml(sql, cond);
        }
    }

    /**
     * 生成XML条件语句
     */
    private void genCondForXml(@NotNull StringBuilder sql, @NotNull SqlNode.SqlCond cond) {
        var col = cond.column;
        sql.append(keyword(col)).append(BLANK).append(cond.op.getSymbol()).append(BLANK);

        Object expr = cond.expr;
        if (expr instanceof SqlOpExpr) {
            sql.append(keyword(col));
        } else {
            switch (cond.op) {
                case IS_NULL, IS_NOT_NULL -> {
                    // 不需要添加占位符
                }
                case BETWEEN, NOT_BETWEEN -> sql.append("? and ?");
                default -> {
                    if (expr instanceof Object[] || expr instanceof java.util.Collection<?>) {
                        sql.append(genPlaceholdersForXml(expr));
                    } else {
                        sql.append("?");
                    }
                }
            }
        }
    }

    /**
     * 生成XML占位符
     */
    private String genPlaceholdersForXml(Object val) {
        StringBuilder ph = new StringBuilder();
        if (val instanceof java.util.Collection<?> ls) {
            genPlaceholderForXml(ph, ls.size());
        } else if (val instanceof Object[] arr) {
            genPlaceholderForXml(ph, arr.length);
        } else {
            ph.append("?");
        }
        return ph.toString();
    }

    /**
     * 生成XML占位符
     */
    private void genPlaceholderForXml(@NotNull StringBuilder ph, int size) {
        ph.append(BRACKET_LEFT);
        for (int i = 0; i < size; i++) {
            ph.append("?");
            if (i != size - 1) {
                ph.append(DELIMITER);
            }
        }
        ph.append(BRACKET_RIGHT);
    }

    /**
     * 获取条件字符串
     *
     * @return 条件字符串
     */
    @Nullable
    public String getConditionString() {
        return StrUtil.isNotBlank(conditionString) ? conditionString : null;
    }

    /**
     * 获取排序字符串
     *
     * @return 排序字符串
     */
    @Nullable
    public String getOrderByString() {
        return StrUtil.isNotBlank(orderByString) ? orderByString : null;
    }

    /**
     * 获取分组字符串
     *
     * @return 分组字符串
     */
    @Nullable
    public String getGroupByString() {
        return StrUtil.isNotBlank(groupByString) ? groupByString : null;
    }

    /**
     * 创建XML查询生成器
     *
     * @param query 查询对象
     * @return XML查询生成器
     */
    @NotNull
    public static XmlQueryGenerator of(@NotNull Query<?, ?> query) {
        return new XmlQueryGenerator(query);
    }
} 