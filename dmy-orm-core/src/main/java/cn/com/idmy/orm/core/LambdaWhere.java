package cn.com.idmy.orm.core;

import cn.com.idmy.orm.core.Node.Cond;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@Slf4j
@Accessors(fluent = true, chain = false)
public abstract class LambdaWhere<T, WHERE extends LambdaWhere<T, WHERE>> extends AbstractWhere<T, WHERE> {
    protected LambdaWhere(Class<T> entityClass) {
        super(entityClass);
    }

    public WHERE addNode(Cond node) {
        switch (node.expr()) {
            case null -> {
                return $this;
            }
            case Collection<?> ls -> {
                if (ls.isEmpty()) {
                    return $this;
                }
            }
            case Object[] arr -> {
                if (arr.length == 0) {
                    return $this;
                }
            }
            default -> {
            }
        }
        super.addNode(node);
        return $this;
    }

    //region 等于
    public WHERE eq(ColumnGetter<T, ?> col, Object val) {
        return addNode(new Cond(col, Op.EQ, val));
    }

    public WHERE eq(ColumnGetter<T, ?> col, SqlOpExpr expr) {
        return addNode(new Cond(col, Op.EQ, expr));
    }

    public WHERE eq(ColumnGetter<T, ?> col, Object val, boolean if0) {
        return if0 ? eq(col, val) : $this;
    }

    public WHERE eq(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        return if0 ? eq(col, expr) : $this;
    }
    //endregion

    //region 不等于
    public WHERE ne(ColumnGetter<T, ?> col, Object val) {
        return addNode(new Cond(col, Op.NE, val));
    }

    public WHERE ne(ColumnGetter<T, ?> col, SqlOpExpr expr) {
        return addNode(new Cond(col, Op.NE, expr));
    }

    public WHERE ne(ColumnGetter<T, ?> col, Object val, boolean if0) {
        return if0 ? ne(col, val) : $this;
    }

    public WHERE ne(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        return if0 ? ne(col, expr) : $this;
    }
    //endregion

    //region 大于 >
    public WHERE gt(ColumnGetter<T, ?> col, Object val) {
        return addNode(new Cond(col, Op.GT, val));
    }

    public WHERE gt(ColumnGetter<T, ?> col, SqlOpExpr expr) {
        return addNode(new Cond(col, Op.GT, expr));
    }

    public WHERE gt(ColumnGetter<T, ?> col, Object val, boolean if0) {
        return if0 ? gt(col, val) : $this;
    }

    public WHERE gt(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        return if0 ? gt(col, expr) : $this;
    }
    //endregion

    //region 大于等于 >=
    public WHERE ge(ColumnGetter<T, ?> col, Object val) {
        return addNode(new Cond(col, Op.GE, val));
    }

    public WHERE ge(ColumnGetter<T, ?> col, SqlOpExpr expr) {
        return addNode(new Cond(col, Op.GE, expr));
    }

    public WHERE ge(ColumnGetter<T, ?> col, Object val, boolean if0) {
        return if0 ? ge(col, val) : $this;
    }

    public WHERE ge(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        return if0 ? ge(col, expr) : $this;
    }
    //endregion

    //region 小于 <
    public WHERE lt(ColumnGetter<T, ?> col, Object val) {
        return addNode(new Cond(col, Op.LT, val));
    }

    public WHERE lt(ColumnGetter<T, ?> col, SqlOpExpr expr) {
        return addNode(new Cond(col, Op.LT, expr));
    }

    public WHERE lt(ColumnGetter<T, ?> col, Object val, boolean if0) {
        return if0 ? lt(col, val) : $this;
    }

    public WHERE lt(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        return if0 ? lt(col, expr) : $this;
    }
    //endregion

    //region 小于等于 <=
    public WHERE le(ColumnGetter<T, ?> col, Object val) {
        return addNode(new Cond(col, Op.LE, val));
    }

    public WHERE le(ColumnGetter<T, ?> col, SqlOpExpr expr) {
        return addNode(new Cond(col, Op.LE, expr));
    }

    public WHERE le(ColumnGetter<T, ?> col, Object val, boolean if0) {
        return if0 ? le(col, val) : $this;
    }

    public WHERE le(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        return if0 ? le(col, expr) : $this;
    }
    //endregion

    //region like
    public WHERE like(ColumnGetter<T, ?> col, String val) {
        return addNode(new Cond(col, Op.LIKE, "%" + val + "%"));
    }

    public WHERE like(ColumnGetter<T, ?> col, String val, boolean if0) {
        return if0 ? like(col, val) : $this;
    }
    //endregion

    //region startsWith
    public WHERE startsWith(ColumnGetter<T, ?> col, String val) {
        return addNode(new Cond(col, Op.LIKE, val + "%"));
    }

    public WHERE startsWith(ColumnGetter<T, ?> col, String val, boolean if0) {
        return if0 ? startsWith(col, val) : $this;
    }
    //endregion

    //region endsWith
    public WHERE endsWith(ColumnGetter<T, ?> col, String val) {
        return addNode(new Cond(col, Op.LIKE, "%" + val));
    }

    public WHERE endsWith(ColumnGetter<T, ?> col, String val, boolean if0) {
        return if0 ? endsWith(col, val) : $this;
    }
    //endregion

    //region in
    public WHERE in(ColumnGetter<T, ?> col, Object val) {
        return addNode(new Cond(col, Op.IN, val));
    }

    public WHERE in(ColumnGetter<T, ?> col, Object... vals) {
        return addNode(new Cond(col, Op.IN, vals));
    }

    public WHERE in(ColumnGetter<T, ?> col, Object val, boolean if0) {
        return if0 ? in(col, val) : $this;
    }

    public WHERE in(ColumnGetter<T, ?> col, Collection<Object> vals, boolean if0) {
        return if0 ? in(col, vals) : $this;
    }
    //endregion

    //region not in
    public WHERE notIn(ColumnGetter<T, ?> col, Object val) {
        return addNode(new Cond(col, Op.IN, val));
    }

    public WHERE notIn(ColumnGetter<T, ?> col, Object... vals) {
        return addNode(new Cond(col, Op.IN, vals));
    }

    public WHERE notIn(ColumnGetter<T, ?> col, Object val, boolean if0) {
        return if0 ? notIn(col, val) : $this;
    }

    public WHERE notIn(ColumnGetter<T, ?> col, Collection<Object> vals, boolean if0) {
        return if0 ? notIn(col, vals) : $this;
    }
    //endregion

    //region nulls
    public WHERE nulls(ColumnGetter<T, ?> col, Boolean bol) {
        if (bol == null) {
            return $this;
        } else if (bol) {
            return addNode(new Cond(col, Op.IS_NULL, null));
        } else {
            return addNode(new Cond(col, Op.IS_NOT_NULL, null));
        }
    }
    //endregion

    //region is null
    public WHERE isNull(ColumnGetter<T, ?> col) {
        return nulls(col, true);
    }

    public WHERE isNull(ColumnGetter<T, ?> col, boolean if0) {
        return nulls(col, if0 ? true : null);
    }
    //endregion

    //region is not null
    public WHERE isNotNull(ColumnGetter<T, ?> col) {
        return nulls(col, false);
    }

    public WHERE isNotNull(ColumnGetter<T, ?> col, boolean if0) {
        return nulls(col, if0 ? false : null);
    }
    //endregion

    //region between
    public WHERE between(ColumnGetter<T, ?> col, Object[] pair) {
        return addNode(new Cond(col, Op.BETWEEN, pair));
    }

    public WHERE between(ColumnGetter<T, ?> col, Object[] pair, boolean if0) {
        return if0 ? between(col, pair) : $this;
    }

    public WHERE between(ColumnGetter<T, ?> col, Object start, Object end) {
        return addNode(new Cond(col, Op.BETWEEN, new Object[]{start, end}));
    }

    public WHERE between(ColumnGetter<T, ?> col, Object start, Object end, boolean if0) {
        return if0 ? between(col, start, end) : $this;
    }
    //endregion

    //region not between
    public WHERE notBetween(ColumnGetter<T, ?> col, Object[] pair) {
        return addNode(new Cond(col, Op.NOT_BETWEEN, pair));
    }

    public WHERE notBetween(ColumnGetter<T, ?> col, Object[] pair, boolean if0) {
        return if0 ? between(col, pair) : $this;
    }

    public WHERE notBetween(ColumnGetter<T, ?> col, Object start, Object end) {
        return addNode(new Cond(col, Op.NOT_BETWEEN, new Object[]{start, end}));
    }

    public WHERE notBetween(ColumnGetter<T, ?> col, Object a, Object b, boolean if0) {
        return if0 ? between(col, a, b) : $this;
    }
    //endregion
}