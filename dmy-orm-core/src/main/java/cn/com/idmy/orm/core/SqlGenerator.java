package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.SqlNode.SqlCond;
import cn.com.idmy.orm.core.SqlNode.SqlNodeType;
import cn.com.idmy.orm.core.SqlNode.SqlOr;
import cn.com.idmy.orm.core.TableInfo.TableColumnInfo;
import cn.com.idmy.orm.mybatis.handler.TypeHandlerValue;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.collection.CollUtil;

import java.util.Collection;
import java.util.List;

import static cn.com.idmy.orm.core.SqlConsts.AND;
import static cn.com.idmy.orm.core.SqlConsts.BLANK;
import static cn.com.idmy.orm.core.SqlConsts.BRACKET_LEFT;
import static cn.com.idmy.orm.core.SqlConsts.BRACKET_RIGHT;
import static cn.com.idmy.orm.core.SqlConsts.DELIMITER;
import static cn.com.idmy.orm.core.SqlConsts.PLACEHOLDER;
import static cn.com.idmy.orm.core.SqlConsts.STRESS_MARK;
import static cn.com.idmy.orm.core.SqlConsts.WHERE;


@Slf4j
@Getter
@Accessors(fluent = true)
public abstract class SqlGenerator {
    protected final Class<?> entityClass;
    protected final String tableName;
    protected final List<SqlNode> nodes;
    protected final StringBuilder sql = new StringBuilder();
    protected List<Object> params;

    public SqlGenerator(Class<?> entityClass, List<SqlNode> notes) {
        this.entityClass = entityClass;
        this.nodes = notes;
        tableName = Tables.getTableName(entityClass);
    }

    protected String warpKeyword(String str) {
        return STRESS_MARK + str + STRESS_MARK;
    }

    protected String genSqlExpr(String col, Object expr, @Nullable Op op) {
        var placeholder = new StringBuilder();
        if (expr instanceof SqlOpExpr e) {
            var sqlOp = e.apply(new SqlOp(col));
            params.add(sqlOp.value());
            return placeholder.append(warpKeyword(sqlOp.column())).append(BLANK).append(sqlOp.op()).append(BLANK).append(PLACEHOLDER).toString();
        } else {
            if (op == Op.BETWEEN || op == Op.NOT_BETWEEN) {
                Object[] arr = (Object[]) expr;
                if (arr.length == 2) {
                    placeholder.append("? and ?");
                } else {
                    throw new OrmException("between 参数必须为2个元素");
                }
            } else {
                genPlaceholder(expr, placeholder);
            }
            params.add(expr);
        }
        return placeholder.toString();
    }

    protected static void genPlaceholder(Object val, StringBuilder placeholder) {
        if (val instanceof Collection<?> ls) {
            genPlaceholder(placeholder, ls.size());
        } else if (val.getClass().isArray()) {
            var arr = (Object[]) val;
            genPlaceholder(placeholder, arr.length);
        } else {
            placeholder.append(PLACEHOLDER);
        }
    }

    protected static void genPlaceholder(StringBuilder placeholder, int size) {
        placeholder.append(BRACKET_LEFT);
        for (int i = 0; i < size; i++) {
            placeholder.append(PLACEHOLDER);
            if (i != size - 1) {
                placeholder.append(DELIMITER);
            }
        }
        placeholder.append(BRACKET_RIGHT);
    }

    protected void genCond(SqlCond cond) {
        var col = cond.column;
        var expr = genSqlExpr(col, cond.expr, cond.op);
        sql.append(warpKeyword(col)).append(BLANK).append(cond.op.getSymbol()).append(BLANK).append(expr);
    }

    protected void genCondOr(SqlNode node) {
        if (node instanceof SqlOr) {
            sql.append(SqlConsts.OR);
        } else if (node instanceof SqlCond cond) {
            genCond(cond);
        }
    }

    protected static void skipAdjoinOr(SqlNode node, List<SqlNode> wheres) {
        if (CollUtil.isNotEmpty(wheres)) {
            if (wheres.getLast().type == SqlNodeType.OR) {
                if (log.isWarnEnabled()) {
                    log.warn("存在相邻的or，已自动移除");
                }
            } else {
                wheres.add(node);
            }
        }
    }

    protected static void removeLastOr(List<SqlNode> wheres) {
        if (CollUtil.isNotEmpty(wheres) && wheres.getLast() instanceof SqlOr) {
            wheres.removeLast();
            if (log.isWarnEnabled()) {
                log.warn("where条件最后存在 or，已自动移除");
            }
        }
    }

    protected void genWhere(List<SqlNode> wheres) {
        if (!wheres.isEmpty()) {
            removeLastOr(wheres);
            sql.append(WHERE);
            for (int i = 0, size = wheres.size(); i < size; i++) {
                var node = wheres.get(i);
                genCondOr(node);
                if (i < size - 1) {
                    if (wheres.get(i + 1).type == SqlNodeType.COND && node.type != SqlNodeType.OR) {
                        sql.append(AND);
                    }
                }
            }
        }
    }

    protected Object getTypeHandlerValue(TableColumnInfo ci, Object val) {
        var th = ci.typeHandler();
        if (th == null) {
            return val;
        } else {
            return new TypeHandlerValue(th, val);
        }
    }

    protected Pair<String, List<Object>> generate() {
        // 根据具体类型调用对应的拦截方法
        switch (this) {
            case UpdateSqlGenerator ignored -> CrudInterceptors.interceptUpdate(entityClass, nodes);
            case DeleteSqlGenerator ignored -> CrudInterceptors.interceptDelete(entityClass, nodes);
            case SelectSqlGenerator ignored -> CrudInterceptors.interceptSelect(entityClass, nodes);
            default -> {
            }
        }
        return doGenerate();
    }

    protected abstract Pair<String, List<Object>> doGenerate();
}
