package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.SqlNode.SqlCond;
import cn.com.idmy.orm.core.SqlNode.SqlNodeType;
import cn.com.idmy.orm.core.SqlNode.SqlOr;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.collection.CollUtil;

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
    protected static final String EQUALS_PLACEHOLDER = " = ? ";

    @NonNull
    protected final Class<?> entityType;
    @NonNull
    protected final String tableName;
    @NonNull
    protected final List<SqlNode> nodes;
    @NonNull
    protected final StringBuilder sql = new StringBuilder();
    protected List<Object> params;

    public SqlGenerator(@NonNull Class<?> entityType, @NonNull List<SqlNode> notes) {
        this.entityType = entityType;
        this.nodes = notes;
        tableName = Tables.getTableName(entityType);
    }

    protected String warpKeyword(@NonNull String str) {
        return STRESS_MARK + str + STRESS_MARK;
    }

    protected static StringBuilder genPlaceholder(@NonNull Object val, @NonNull StringBuilder placeholder) {
        if (val instanceof Collection<?> ls) {
            genPlaceholder(placeholder, ls.size());
        } else if (val instanceof Object[] arr) {
            genPlaceholder(placeholder, arr.length);
        } else {
            placeholder.append(PLACEHOLDER);
        }
        return placeholder;
    }

    protected static void genPlaceholder(@NonNull StringBuilder placeholder, int size) {
        placeholder.append(BRACKET_LEFT);
        for (int i = 0; i < size; i++) {
            placeholder.append(PLACEHOLDER);
            if (i != size - 1) {
                placeholder.append(DELIMITER);
            }
        }
        placeholder.append(BRACKET_RIGHT);
    }

    protected String genCond(@NonNull String col, @NonNull SqlOpExpr expr) {
        var sqlOp = expr.apply(new SqlOp<>());
        params.add(sqlOp.value());
        return warpKeyword(col) + BLANK + sqlOp.op() + BLANK + PLACEHOLDER;
    }

    protected String genCond(@NonNull Op op, @NonNull Object value) {
        var placeholder = new StringBuilder();
        if (op == Op.IS_NULL || op == Op.IS_NOT_NULL) {
            return PLACEHOLDER;
        } else if (op == Op.BETWEEN || op == Op.NOT_BETWEEN) {
            Object[] arr = (Object[]) value;
            if (arr.length == 2) {
                params.add(value);
                return "? and ?";
            } else {
                throw new OrmException("between 参数必须为2个元素");
            }
        } else {
            params.add(value);
            return genPlaceholder(value, placeholder).toString();
        }
    }

    protected void genCond(@NonNull SqlCond cond) {
        var col = cond.column;
        String str;
        Object expr = cond.expr;
        if (expr instanceof SqlOpExpr e) {
            str = genCond(cond.column, e);
        } else {
            str = genCond(cond.op, expr);
        }
        sql.append(warpKeyword(col)).append(BLANK).append(cond.op.getSymbol()).append(BLANK).append(str);
    }

    protected void genCondOr(@NonNull SqlNode node) {
        if (node instanceof SqlOr) {
            sql.append(OR);
        } else if (node instanceof SqlCond cond) {
            genCond(cond);
        }
    }

    protected static void skipAdjoinOr(@NonNull SqlNode node, @NonNull List<SqlNode> wheres) {
        if (CollUtil.isNotEmpty(wheres)) {
            if (wheres.getLast().type == SqlNodeType.OR) {
                if (log.isWarnEnabled()) {
                    log.warn("存在相邻的 or，已自动移除");
                }
            } else {
                wheres.add(node);
            }
        }
    }

    protected static void removeLastOr(@NonNull List<SqlNode> wheres) {
        if (CollUtil.isNotEmpty(wheres) && wheres.getLast() instanceof SqlOr) {
            wheres.removeLast();
            if (log.isWarnEnabled()) {
                log.warn("where 条件最后存在 or，已自动移除");
            }
        }
    }

    protected boolean genWhere(@NonNull List<SqlNode> wheres) {
        if (wheres.isEmpty()) {
            return true;
        } else {
            boolean out = true;
            removeLastOr(wheres);
            sql.append(WHERE);
            for (int i = 0, size = wheres.size(); i < size; i++) {
                var node = wheres.get(i);
                genCondOr(node);
                out = false;
                if (i < size - 1) {
                    if (wheres.get(i + 1).type == SqlNodeType.COND && node.type != SqlNodeType.OR) {
                        sql.append(AND);
                    }
                }
            }
            return out;
        }
    }

    @NonNull
    protected Pair<String, List<Object>> generate() {
        // 根据具体类型调用对应的拦截方法
        switch (this) {
            case UpdateSqlGenerator ignored -> CrudInterceptors.interceptUpdate(entityType, nodes);
            case DeleteSqlGenerator ignored -> CrudInterceptors.interceptDelete(entityType, nodes);
            case QuerySqlGenerator ignored -> CrudInterceptors.interceptQuery(entityType, nodes);
            default -> {
            }
        }
        return doGenerate();
    }

    @NonNull
    protected abstract Pair<String, List<Object>> doGenerate();
}
