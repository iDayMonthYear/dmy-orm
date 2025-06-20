package cn.com.idmy.orm.core;

import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.SqlNode.SqlBracket;
import cn.com.idmy.orm.core.SqlNode.SqlCond;
import cn.com.idmy.orm.core.SqlNode.SqlOr;
import cn.com.idmy.orm.core.SqlNode.Type;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.lang.tuple.Pair;
import org.dromara.hutool.core.util.ObjUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;


@Slf4j
@Getter
@Accessors(fluent = true)
public abstract class SqlGenerator {
    protected static final String EMPTY = "";
    protected static final String BLANK = " ";
    protected static final String ASTERISK = "*";
    protected static final String DELIMITER = ", ";
    protected static final String PLACEHOLDER = "?";
    protected static final String STRESS_MARK = "`";
    protected static final String BRACKET_LEFT = "(";
    protected static final String BRACKET_RIGHT = ")";
    protected static final String OR = " or ";
    protected static final String AND = " and ";
    protected static final String SET = " set ";
    protected static final String FROM = " from ";
    protected static final String WHERE = " where ";
    protected static final String SELECT = "select ";
    protected static final String VALUES = " values ";
    protected static final String DELETE = "delete";
    protected static final String UPDATE = "update ";
    protected static final String DISTINCT = "distinct ";
    protected static final String GROUP_BY = " group by ";
    protected static final String ORDER_BY = " order by ";
    protected static final String INSERT = "insert";
    protected static final String INTO = " into ";
    protected static final String INSERT_INTO = INSERT + INTO;
    protected static final String DELETE_FROM = DELETE + FROM;
    protected static final String LIMIT = " limit ";
    protected static final String OFFSET = " offset ";
    protected static final String DESC = " desc";
    protected static final String EQUAL = " = ";
    protected static final String BETWEEN = "? and ?";

    protected final @NotNull Class<?> entityType;
    protected final @NotNull TableInfo tableInfo;
    protected final @NotNull List<SqlNode> nodes;
    protected final @NotNull StringBuilder sql = new StringBuilder();
    protected List<Object> values;

    public SqlGenerator(@NotNull Class<?> entityType, @NotNull List<SqlNode> notes) {
        this.entityType = entityType;
        this.nodes = notes;
        tableInfo = Tables.getTable(entityType);
    }

    protected static StringBuilder genPlaceholder(@NonNull Object val, @NonNull StringBuilder ph) {
        if (val instanceof Collection<?> ls) {
            genPlaceholder(ph, ls.size());
        } else if (val instanceof Object[] arr) {
            genPlaceholder(ph, arr.length);
        } else {
            ph.append(PLACEHOLDER);
        }
        return ph;
    }

    protected static void genPlaceholder(@NonNull StringBuilder ph, int size) {
        ph.append(BRACKET_LEFT);
        for (int i = 0; i < size; i++) {
            ph.append(PLACEHOLDER);
            if (i != size - 1) {
                ph.append(DELIMITER);
            }
        }
        ph.append(BRACKET_RIGHT);
    }

    protected static void skipAdjoinOr(@NonNull List<SqlNode> wheres) {
        if (CollUtil.isNotEmpty(wheres)) {
            if (wheres.getLast() == SqlOr.OR) {
                if (log.isDebugEnabled()) {
                    log.warn("存在相邻的 or，已自动移除");
                }
            } else {
                wheres.add(SqlOr.OR);
            }
        }
    }

    protected static void removeLastOr(@NonNull List<SqlNode> ls) {
        if (CollUtil.isNotEmpty(ls) && ls.getLast() == SqlOr.OR) {
            ls.removeLast();
            if (log.isDebugEnabled()) {
                log.warn("where 条件最后存在 or，已自动移除");
            }
        }
    }

    protected String keyword(@NonNull String val) {
        return STRESS_MARK + val + STRESS_MARK;
    }

    protected String genCond(@NonNull String col, @NonNull SqlOpExpr expr) {
        var sqlOp = expr.op(new SqlOp<>());
        values.add(sqlOp.value());
        return keyword(col) + BLANK + sqlOp.op() + BLANK + PLACEHOLDER;
    }

    protected String genCond(@NonNull Op op, @NonNull Object val) {
        var placeholder = new StringBuilder();
        if (op == Op.IS_NULL || op == Op.IS_NOT_NULL) {
            return EMPTY;
        } else if (op == Op.BETWEEN || op == Op.NOT_BETWEEN) {
            var arr = (Object[]) val;
            if (arr.length == 2) {
                if (ObjUtil.isEmpty(arr[0])) {
                    throw new OrmException("between 第一个参数不能为空");
                }
                if (ObjUtil.isEmpty(arr[1])) {
                    throw new OrmException("between 第二个参数不能为空");
                }
                values.add(val);
                return BETWEEN;
            } else {
                throw new OrmException("between 参数必须为 2 个元素");
            }
        } else {
            values.add(val);
            return genPlaceholder(val, placeholder).toString();
        }
    }

    protected void genCond(@NonNull SqlNode.SqlCond cond) {
        var col = cond.column;
        String str;
        Object expr = cond.expr;
        if (expr instanceof SqlOpExpr e) {
            str = genCond(cond.column, e);
        } else {
            str = genCond(cond.op, expr);
        }
        sql.append(keyword(col)).append(BLANK).append(cond.op.getSymbol()).append(BLANK).append(str);
    }

    protected void genWhereNode(@NonNull SqlNode node) {
        if (node == SqlOr.OR) {
            sql.append(OR);
        } else if (node instanceof SqlCond cond) {
            genCond(cond);
        } else if (node == SqlBracket.LEFT) {
            sql.append(BRACKET_LEFT);
        } else if (node == SqlBracket.RIGHT) {
            sql.append(BRACKET_RIGHT);
        }
    }

    protected void genWhere(@NonNull List<SqlNode> ls) {
        if (ls.isEmpty()) {
            return;
        }
        removeLastOr(ls);
        sql.append(WHERE);

        for (int i = 0, size = ls.size(); i < size; i++) {
            var node = ls.get(i);
            // 处理节点
            genWhereNode(node);
            // 处理连接符逻辑
            if (i < size - 1) {
                var nextNode = ls.get(i + 1);
                // 只有在以下条件都满足时才添加AND:
                // 1. 当前节点不是左括号或OR
                // 2. 下一个节点不是右括号或OR
                if ((node.type != Type.LEFT_BRACKET && node.type != Type.OR) && (nextNode.type != Type.RIGHT_BRACKET && nextNode.type != Type.OR)) {
                    sql.append(AND);
                }
            }
        }
    }

    protected @NotNull Pair<String, List<Object>> generate() {
        switch (this) {
            case UpdateSqlGenerator ignored -> CrudInterceptors.interceptUpdate(entityType, nodes);
            case DeleteSqlGenerator ignored -> CrudInterceptors.interceptDelete(entityType, nodes);
            case QuerySqlGenerator ignored -> CrudInterceptors.interceptQuery(entityType, nodes);
            default -> {
            }
        }
        return doGenerate();
    }

    protected abstract @NotNull Pair<String, List<Object>> doGenerate();
}
