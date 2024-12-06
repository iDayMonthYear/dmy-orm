package cn.com.idmy.orm.core;

import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.Node.*;
import cn.com.idmy.orm.core.TableInfo.TableColumnInfo;
import cn.com.idmy.orm.mybatis.handler.TypeHandlerValue;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.collection.CollUtil;

import java.util.Collection;
import java.util.List;

import static cn.com.idmy.orm.core.SqlConsts.*;


@Slf4j
@RequiredArgsConstructor
abstract class SqlGenerator {
    protected final Class<?> entityClass;
    protected List<Object> params;
    protected StringBuilder sql = new StringBuilder();

    protected static String warpKeyword(String str) {
        return STRESS_MARK + str + STRESS_MARK;
    }

    protected String buildSqlExpr(String col, Object expr, @Nullable Op op) {
        StringBuilder placeholder = new StringBuilder();
        if (expr instanceof SqlOpExpr sqlOpExpr) {
            SqlOp sqlOp = sqlOpExpr.apply(new SqlOp(col));
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
        var col = cond.column;
        var expr = buildSqlExpr(col, cond.expr, cond.op);
        return sql.append(warpKeyword(col)).append(BLANK).append(cond.op.getSymbol()).append(BLANK).append(expr);
    }

    protected StringBuilder buildSet(Set set) {
        var col = set.column;
        var expr = buildSqlExpr(col, set.expr, null);
        TableInfo table = Tables.getTable(entityClass);
        if (table != null) {
            var map = table.columnMap();
            if (CollUtil.isNotEmpty(map)) {
                TableColumnInfo info = map.get(col);
                var typeHandler = info.typeHandler();
                if (typeHandler != null) {
                    Object o = params.removeLast();
                    params.add(new TypeHandlerValue(typeHandler, o));
                }
            }
        }
        return sql.append(warpKeyword(col)).append(EQUAL).append(expr);
    }

    protected String buildSelectColumn(SelectColumn selectColumn) {
        String col = warpKeyword(selectColumn.column);
        if (selectColumn.expr != null) {
            var expr = selectColumn.expr;
            var fn = expr.apply();
            var name = fn.name();
            if (name == SqlFnName.IF_NULL) {
                params.add(fn.value());
                sql.append(name.getName()).append(BRACKET_LEFT).append(col).append(DELIMITER).append(PLACEHOLDER).append(BRACKET_RIGHT).append(BLANK).append(col);
            } else {
                sql.append(name.getName()).append(BRACKET_LEFT).append(col).append(BRACKET_RIGHT).append(BLANK).append(col);
            }
        } else {
            sql.append(col);
        }
        return selectColumn.column;
    }

    protected StringBuilder buildGroupBy(GroupBy group) {
        sql.append(warpKeyword(group.column));
        return sql;
    }

    protected StringBuilder buildOrderBy(OrderBy order) {
        return sql.append(warpKeyword(order.column)).append(order.desc ? DESC : EMPTY);
    }

    protected StringBuilder buildDistinct(Distinct distinct) {
        var col = distinct.column;
        if (col == null) {
            return sql.append(DISTINCT);
        } else {
            return sql.append(DISTINCT).append(BRACKET_LEFT).append(warpKeyword(col)).append(BRACKET_RIGHT);
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
                if (log.isWarnEnabled()) {
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
            if (log.isWarnEnabled()) {
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
