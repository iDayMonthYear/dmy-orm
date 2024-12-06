package cn.com.idmy.orm.core;

import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.Node.*;
import cn.com.idmy.orm.core.TableInfo.TableColumnInfo;
import cn.com.idmy.orm.mybatis.handler.TypeHandlerValue;
import cn.com.idmy.orm.util.SqlUtil;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.func.LambdaUtil;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static cn.com.idmy.orm.core.SqlConsts.*;
import static cn.com.idmy.orm.core.SqlFnName.COUNT;


@Slf4j
@RequiredArgsConstructor
abstract class SqlGenerator {
    protected final Class<?> entityClass;
    protected List<Object> params;
    protected StringBuilder sql = new StringBuilder();

    protected static String warpKeyword(String str) {
        return STRESS_MARK + str + STRESS_MARK;
    }

    protected static String buildColumn(Object col) {
        if (col instanceof ColumnGetter<?, ?> getter) {
            return LambdaUtil.getFieldName(getter);
        } else {
            SqlUtil.checkColumn((String) col);
            return (String) col;
        }
    }

    protected static String buildSqlFn(SqlFn<?> fn) {
        SqlFnName name = fn.name();
        if (name == COUNT && fn.column() == null) {
            return ASTERISK;
        } else {
            return warpKeyword(buildColumn(fn.column()));
        }
    }

    protected String buildSqlExpr(String column, Object expr, @Nullable Op op) {
        StringBuilder placeholder = new StringBuilder();
        if (expr instanceof SqlOpExpr sqlOpExpr) {
            SqlOp sqlOp = sqlOpExpr.apply(new SqlOp(column));
            params.add(sqlOp.value());
            return placeholder.append(warpKeyword(sqlOp.column())).append(BLANK).append(sqlOp.op()).append(BLANK).append(PLACEHOLDER).toString();
        } else {
            if (op == Op.BETWEEN || op == Op.NOT_BETWEEN) {
                Object[] arr = (Object[]) expr;
                if (arr.length == 2) {
                    placeholder.append("? and ?");
                } else {
                    throw new OrmException("between参数必须为2个元素");
                }
            } else {
                buildPlaceholder(expr, placeholder);
            }
            params.add(expr);
        }
        return placeholder.toString();
    }

    protected static void buildPlaceholder(Object value, StringBuilder placeholder) {
        if (value instanceof Collection<?> ls) {
            buildPlaceholder(placeholder, ls.size());
        } else if (value.getClass().isArray()) {
            var arr = (Object[]) value;
            buildPlaceholder(placeholder, arr.length);
        } else {
            placeholder.append(PLACEHOLDER);
        }
    }

    protected static void buildPlaceholder(StringBuilder placeholder, int size) {
        placeholder.append(BRACKET_LEFT);
        for (int i = 0; i < size; i++) {
            placeholder.append(PLACEHOLDER);
            if (i != size - 1) {
                placeholder.append(DELIMITER);
            }
        }
        placeholder.append(BRACKET_RIGHT);
    }

    protected StringBuilder buildCond(Cond cond) {
        var column = buildColumn(cond.column);
        var expr = buildSqlExpr(column, cond.expr, cond.op);
        return sql.append(warpKeyword(column)).append(BLANK).append(cond.op.getSymbol()).append(BLANK).append(expr);
    }

    protected StringBuilder buildSet(Set set) {
        var column = buildColumn(set.column);
        var expr = buildSqlExpr(column, set.expr, null);
        TableInfo table = Tables.getTable(entityClass);
        if (table != null) {
            var map = table.columnMap();
            if (CollUtil.isNotEmpty(map)) {
                TableColumnInfo info = map.get(column);
                var typeHandler = info.typeHandler();
                if (typeHandler != null) {
                    Object o = params.removeLast();
                    params.add(new TypeHandlerValue(typeHandler, o));
                }
            }
        }
        return sql.append(warpKeyword(column)).append(EQUAL).append(expr);
    }

    protected String buildSelectColumn(SelectColumn selectColumn) {
        var column = selectColumn.column;
        if (column instanceof ColumnGetter<?, ?> || column instanceof String) {
            var out = buildColumn(column);
            sql.append(warpKeyword(out));
            return out;
        } else {
            var expr = (SqlFnExpr<?>) column;
            var fn = expr.apply();
            var col = buildSqlFn(fn);
            var alias = selectColumn.alias == null ? null : LambdaUtil.getFieldName(selectColumn.alias);
            var colOrAlias = Objects.requireNonNullElse(alias, col.equals(ASTERISK) ? BLANK : col);
            var name = fn.name();
            if (name == SqlFnName.IF_NULL) {
                params.add(fn.value());
                sql.append(name.getName()).append(BRACKET_LEFT).append(col).append(DELIMITER).append(PLACEHOLDER).append(BRACKET_RIGHT).append(BLANK).append(colOrAlias);
            } else {
                sql.append(name.getName()).append(BRACKET_LEFT).append(col).append(BRACKET_RIGHT).append(BLANK).append(colOrAlias);
            }
            return colOrAlias;
        }
    }

    protected StringBuilder buildGroupBy(GroupBy group) {
        sql.append(warpKeyword(buildColumn(group.column)));
        return sql;
    }

    protected StringBuilder buildOrderBy(OrderBy order) {
        Object column = order.column;
        String name;
        if (column instanceof ColumnGetter<?, ?> getter) {
            name = buildColumn(getter);
        } else {
            name = (String) column;
            SqlUtil.checkColumn(name); //字符串类型可以是前端过来的。必须检查
        }
        return sql.append(warpKeyword(name)).append(order.desc ? DESC : EMPTY);
    }

    protected StringBuilder buildDistinct(Distinct distinct) {
        var column = distinct.column;
        if (column == null) {
            return sql.append(DISTINCT);
        } else {
            return sql.append(DISTINCT).append(BRACKET_LEFT).append(warpKeyword(buildColumn(column))).append(BRACKET_RIGHT);
        }
    }

    @Nullable
    protected Object builder(Node node) {
        return switch (node) {
            case Node.Or ignored -> sql.append(SqlConsts.OR);
            case Node.Cond cond -> buildCond(cond);
            case Node.Set set -> buildSet(set);
            case Node.GroupBy group -> buildGroupBy(group);
            case Node.OrderBy order -> buildOrderBy(order);
            case Node.SelectColumn col -> buildSelectColumn(col);
            case Node.Distinct distinct -> buildDistinct(distinct);
            case null, default -> null;
        };
    }

    protected static void skipAdjoinOr(Node node, List<Node> wheres) {
        if (CollUtil.isNotEmpty(wheres)) {
            if (wheres.getLast().type == Type.OR) {
                if (log.isDebugEnabled()) {
                    log.warn("存在相邻的or，已自动移除");
                }
            } else {
                wheres.add(node);
            }
        }
    }

    protected static void removeLastOr(List<Node> wheres) {
        if (CollUtil.isNotEmpty(wheres) && wheres.getLast() instanceof Or) {
            wheres.removeLast();
            if (log.isDebugEnabled()) {
                log.warn("where条件最后存在 or，已自动移除");
            }
        }
    }

    protected void buildWhere(List<Node> wheres) {
        if (!wheres.isEmpty()) {
            removeLastOr(wheres);
            sql.append(WHERE);
            for (int i = 0, size = wheres.size(); i < size; i++) {
                Node node = wheres.get(i);
                builder(node);
                if (i < size - 1) {
                    Type type = wheres.get(i + 1).type;
                    if (type == Type.COND && node.type != Type.OR) {
                        sql.append(AND);
                    }
                }
            }
        }
    }
}
