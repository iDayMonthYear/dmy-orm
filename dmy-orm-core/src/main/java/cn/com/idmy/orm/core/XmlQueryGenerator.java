package cn.com.idmy.orm.core;

import cn.com.idmy.orm.core.SqlNode.SqlOr;
import cn.com.idmy.orm.core.SqlNode.Type;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.text.StrUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * XML查询生成器，用于生成XML查询所需的条件、排序和分组字符串
 */
@Slf4j
public class XmlQueryGenerator extends QuerySqlGenerator {
    private String whereString;
    private String orderByString;
    private String groupByString;

    protected XmlQueryGenerator(@NotNull Query<?> query) {
        super(query);
        values = new ArrayList<>();
        generateQueryStrings();
    }

    private void generateQueryStrings() {
        // 收集条件节点
        var wheres = new ArrayList<SqlNode>(nodes.size());
        for (int i = 0, size = nodes.size(); i < size; i++) {
            var node = nodes.get(i);
            if (node instanceof SqlNode.SqlCond || node instanceof SqlNode.SqlBracket) {
                wheres.add(node);
            } else if (node == SqlOr.OR) {
                skipAdjoinOr(wheres);
            }
        }

        // 生成条件字符串
        if (!wheres.isEmpty()) {
            var condSql = new StringBuilder();
            removeLastOr(wheres);
            for (int i = 0, size = wheres.size(); i < size; i++) {
                var node = wheres.get(i);
                genWhereForXml(condSql, node);
                // 处理连接符逻辑
                if (i < size - 1) {
                    var nextNode = wheres.get(i + 1);
                    // 只有在以下条件都满足时才添加AND:
                    // 1. 当前节点不是左括号或OR
                    // 2. 下一个节点不是右括号或OR
                    if ((node.type != Type.LEFT_BRACKET && node.type != Type.OR) && (nextNode.type != Type.RIGHT_BRACKET && nextNode.type != Type.OR)) {
                        condSql.append(AND);
                    }
                }
            }
            this.whereString = condSql.toString();
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
            var orderSql = new StringBuilder();
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
            var groupSql = new StringBuilder();
            for (int i = 0, size = groups.size(); i < size; i++) {
                groupSql.append(keyword(groups.get(i).column));
                if (i < size - 1) {
                    groupSql.append(DELIMITER);
                }
            }
            this.groupByString = groupSql.toString();
        }
    }

    private void genWhereForXml(@NotNull StringBuilder sql, @NotNull SqlNode node) {
        switch (node) {
            case SqlOr ignored -> sql.append(OR);
            case SqlNode.SqlCond cond -> genWhereForXml(sql, cond);
            case SqlNode.SqlBracket ignored -> {
                if (node == SqlNode.SqlBracket.LEFT) {
                    sql.append(BRACKET_LEFT);
                } else {
                    sql.append(BRACKET_RIGHT);
                }
            }
            default -> {
            }
        }
    }

    private void genWhereForXml(@NotNull StringBuilder sql, @NotNull SqlNode.SqlCond cond) {
        var col = cond.column;

        sql.append(keyword(tableInfo.name())).append(".").append(keyword(col)).append(BLANK).append(cond.op.getSymbol()).append(BLANK);

        var expr = cond.expr;
        if (expr instanceof SqlOpExpr) {
            sql.append(keyword(col));
        } else {
            switch (cond.op) {
                case IS_NULL, IS_NOT_NULL -> {
                    // 不需要添加占位符
                }
                case BETWEEN, NOT_BETWEEN -> {
                    if (expr instanceof Object[] arr && arr.length == 2) {
                        sql.append("#{values[").append(values.size()).append("]} and #{values[").append(values.size() + 1).append("]}");
                        values.add(arr[0]);
                        values.add(arr[1]);
                    }
                }
                case IN, NOT_IN -> {
                    if (expr instanceof Object[] arr) {
                        sql.append("(");
                        for (int i = 0; i < arr.length; i++) {
                            sql.append("#{values[").append(values.size()).append("]}");
                            values.add(arr[i]);
                            if (i < arr.length - 1) {
                                sql.append(DELIMITER);
                            }
                        }
                        sql.append(")");
                    } else if (expr instanceof Collection<?> coll) {
                        sql.append("(");
                        int i = 0;
                        for (var item : coll) {
                            sql.append("#{values[").append(values.size()).append("]}");
                            values.add(item);
                            if (i < coll.size() - 1) {
                                sql.append(DELIMITER);
                            }
                            i++;
                        }
                        sql.append(")");
                    }
                }
                default -> {
                    sql.append("#{values[").append(values.size()).append("]}");
                    values.add(expr);
                }
            }
        }
    }

    public @Nullable String getWhereString() {
        return StrUtil.isNotBlank(whereString) ? whereString : null;
    }

    public @Nullable String getOrderByString() {
        return StrUtil.isNotBlank(orderByString) ? orderByString : null;
    }

    public @Nullable String getGroupByString() {
        return StrUtil.isNotBlank(groupByString) ? groupByString : null;
    }

    public static @NotNull XmlQueryGenerator of(@NotNull Query<?> query) {
        return new XmlQueryGenerator(query);
    }
} 