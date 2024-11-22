package cn.com.idmy.orm.core;

import cn.com.idmy.orm.core.Node.Cond;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@Slf4j
@Accessors(fluent = true, chain = false)
public abstract class Where<T, RUD extends Where<T, RUD>> extends Rud<T, RUD> {
    protected Where(Class<T> entityClass) {
        super(entityClass);
    }

    public RUD addNode(Cond node) {
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
    public RUD eq(ColumnGetter<T, ?> col, Object val) {
        return addNode(new Cond(col, Op.EQ, val));
    }

    public RUD eq(ColumnGetter<T, ?> col, SqlOpExpr expr) {
        return addNode(new Cond(col, Op.EQ, expr));
    }

    public RUD eq(ColumnGetter<T, ?> col, Object val, boolean if0) {
        return if0 ? eq(col, val) : $this;
    }

    public RUD eq(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        return if0 ? eq(col, expr) : $this;
    }
    //endregion

    //region 不等于
    public RUD ne(ColumnGetter<T, ?> col, Object val) {
        return addNode(new Cond(col, Op.NE, val));
    }

    public RUD ne(ColumnGetter<T, ?> col, SqlOpExpr expr) {
        return addNode(new Cond(col, Op.NE, expr));
    }

    public RUD ne(ColumnGetter<T, ?> col, Object val, boolean if0) {
        return if0 ? ne(col, val) : $this;
    }

    public RUD ne(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        return if0 ? ne(col, expr) : $this;
    }
    //endregion

    //region 大于 >
    public RUD gt(ColumnGetter<T, ?> col, Object val) {
        return addNode(new Cond(col, Op.GT, val));
    }

    public RUD gt(ColumnGetter<T, ?> col, SqlOpExpr expr) {
        return addNode(new Cond(col, Op.GT, expr));
    }

    public RUD gt(ColumnGetter<T, ?> col, Object val, boolean if0) {
        return if0 ? gt(col, val) : $this;
    }

    public RUD gt(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        return if0 ? gt(col, expr) : $this;
    }
    //endregion

    //region 大于等于 >=
    public RUD ge(ColumnGetter<T, ?> col, Object val) {
        return addNode(new Cond(col, Op.GE, val));
    }

    public RUD ge(ColumnGetter<T, ?> col, SqlOpExpr expr) {
        return addNode(new Cond(col, Op.GE, expr));
    }

    public RUD ge(ColumnGetter<T, ?> col, Object val, boolean if0) {
        return if0 ? ge(col, val) : $this;
    }

    public RUD ge(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        return if0 ? ge(col, expr) : $this;
    }
    //endregion

    //region 小于 <
    public RUD lt(ColumnGetter<T, ?> col, Object val) {
        return addNode(new Cond(col, Op.LT, val));
    }

    public RUD lt(ColumnGetter<T, ?> col, SqlOpExpr expr) {
        return addNode(new Cond(col, Op.LT, expr));
    }

    public RUD lt(ColumnGetter<T, ?> col, Object val, boolean if0) {
        return if0 ? lt(col, val) : $this;
    }

    public RUD lt(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        return if0 ? lt(col, expr) : $this;
    }
    //endregion

    //region 小于等于 <=
    public RUD le(ColumnGetter<T, ?> col, Object val) {
        return addNode(new Cond(col, Op.LE, val));
    }

    public RUD le(ColumnGetter<T, ?> col, SqlOpExpr expr) {
        return addNode(new Cond(col, Op.LE, expr));
    }

    public RUD le(ColumnGetter<T, ?> col, Object val, boolean if0) {
        return if0 ? le(col, val) : $this;
    }

    public RUD le(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        return if0 ? le(col, expr) : $this;
    }
    //endregion

    //region like
    public RUD like(ColumnGetter<T, ?> col, String val) {
        return addNode(new Cond(col, Op.LIKE, "%" + val + "%"));
    }

    public RUD like(ColumnGetter<T, ?> col, String val, boolean if0) {
        return if0 ? like(col, val) : $this;
    }
    //endregion

    //region startsWith
    public RUD startsWith(ColumnGetter<T, ?> col, String val) {
        return addNode(new Cond(col, Op.LIKE, val + "%"));
    }

    public RUD startsWith(ColumnGetter<T, ?> col, String val, boolean if0) {
        return if0 ? startsWith(col, val) : $this;
    }
    //endregion

    //region endsWith
    public RUD endsWith(ColumnGetter<T, ?> col, String val) {
        return addNode(new Cond(col, Op.LIKE, "%" + val));
    }

    public RUD endsWith(ColumnGetter<T, ?> col, String val, boolean if0) {
        return if0 ? endsWith(col, val) : $this;
    }
    //endregion

    //region in
    public RUD in(ColumnGetter<T, ?> col, Object val) {
        return addNode(new Cond(col, Op.IN, val));
    }

    public RUD in(ColumnGetter<T, ?> col, Object... vals) {
        return addNode(new Cond(col, Op.IN, vals));
    }

    public RUD in(ColumnGetter<T, ?> col, Object val, boolean if0) {
        return if0 ? in(col, val) : $this;
    }

    public RUD in(ColumnGetter<T, ?> col, Collection<Object> vals, boolean if0) {
        return if0 ? in(col, vals) : $this;
    }
    //endregion

    //region not in
    public RUD notIn(ColumnGetter<T, ?> col, Object val) {
        return addNode(new Cond(col, Op.IN, val));
    }

    public RUD notIn(ColumnGetter<T, ?> col, Object... vals) {
        return addNode(new Cond(col, Op.IN, vals));
    }

    public RUD notIn(ColumnGetter<T, ?> col, Object val, boolean if0) {
        return if0 ? notIn(col, val) : $this;
    }

    public RUD notIn(ColumnGetter<T, ?> col, Collection<Object> vals, boolean if0) {
        return if0 ? notIn(col, vals) : $this;
    }
    //endregion

    //region nulls
    public RUD nulls(ColumnGetter<T, ?> col, Boolean bol) {
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
    public RUD isNull(ColumnGetter<T, ?> col) {
        return nulls(col, true);
    }

    public RUD isNull(ColumnGetter<T, ?> col, boolean if0) {
        return nulls(col, if0 ? true : null);
    }
    //endregion

    //region is not null
    public RUD isNotNull(ColumnGetter<T, ?> col) {
        return nulls(col, false);
    }

    public RUD isNotNull(ColumnGetter<T, ?> col, boolean if0) {
        return nulls(col, if0 ? false : null);
    }
    //endregion

    //region between
    public RUD between(ColumnGetter<T, ?> col, Object[] pair) {
        return addNode(new Cond(col, Op.BETWEEN, pair));
    }

    public RUD between(ColumnGetter<T, ?> col, Object[] pair, boolean if0) {
        return if0 ? between(col, pair) : $this;
    }

    public RUD between(ColumnGetter<T, ?> col, Object start, Object end) {
        return addNode(new Cond(col, Op.BETWEEN, new Object[]{start, end}));
    }

    public RUD between(ColumnGetter<T, ?> col, Object start, Object end, boolean if0) {
        return if0 ? between(col, start, end) : $this;
    }
    //endregion

    //region not between
    public RUD notBetween(ColumnGetter<T, ?> col, Object[] pair) {
        return addNode(new Cond(col, Op.NOT_BETWEEN, pair));
    }

    public RUD notBetween(ColumnGetter<T, ?> col, Object[] pair, boolean if0) {
        return if0 ? between(col, pair) : $this;
    }

    public RUD notBetween(ColumnGetter<T, ?> col, Object start, Object end) {
        return addNode(new Cond(col, Op.NOT_BETWEEN, new Object[]{start, end}));
    }

    public RUD notBetween(ColumnGetter<T, ?> col, Object a, Object b, boolean if0) {
        return if0 ? between(col, a, b) : $this;
    }
    //endregion
}