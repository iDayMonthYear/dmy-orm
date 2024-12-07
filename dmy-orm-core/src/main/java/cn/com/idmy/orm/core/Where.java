package cn.com.idmy.orm.core;

import cn.com.idmy.orm.core.SqlNode.SqlCond;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@Slf4j
@Accessors(fluent = true, chain = false)
public abstract class Where<T, SUD extends Where<T, SUD>> extends Crud<T, SUD> {
    protected Where(Class<T> entityClass) {
        super(entityClass);
    }

    public SUD addNode(SqlCond node) {
        switch (node.expr) {
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
        return super.addNode(node);
    }

    //region 等于
    public SUD eq(ColumnGetter<T, ?> col, Object val) {
        return addNode(new SqlCond(col, Op.EQ, val));
    }

    public SUD eq(ColumnGetter<T, ?> col, SqlOpExpr expr) {
        return addNode(new SqlCond(col, Op.EQ, expr));
    }

    public SUD eq(ColumnGetter<T, ?> col, Object val, boolean if0) {
        return if0 ? eq(col, val) : $this;
    }

    public SUD eq(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        return if0 ? eq(col, expr) : $this;
    }
    //endregion

    //region 不等于
    public SUD ne(ColumnGetter<T, ?> col, Object val) {
        return addNode(new SqlCond(col, Op.NE, val));
    }

    public SUD ne(ColumnGetter<T, ?> col, SqlOpExpr expr) {
        return addNode(new SqlCond(col, Op.NE, expr));
    }

    public SUD ne(ColumnGetter<T, ?> col, Object val, boolean if0) {
        return if0 ? ne(col, val) : $this;
    }

    public SUD ne(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        return if0 ? ne(col, expr) : $this;
    }
    //endregion

    //region 大于 >
    public SUD gt(ColumnGetter<T, ?> col, Object val) {
        return addNode(new SqlCond(col, Op.GT, val));
    }

    public SUD gt(ColumnGetter<T, ?> col, SqlOpExpr expr) {
        return addNode(new SqlCond(col, Op.GT, expr));
    }

    public SUD gt(ColumnGetter<T, ?> col, Object val, boolean if0) {
        return if0 ? gt(col, val) : $this;
    }

    public SUD gt(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        return if0 ? gt(col, expr) : $this;
    }
    //endregion

    //region 大于等于 >=
    public SUD ge(ColumnGetter<T, ?> col, Object val) {
        return addNode(new SqlCond(col, Op.GE, val));
    }

    public SUD ge(ColumnGetter<T, ?> col, SqlOpExpr expr) {
        return addNode(new SqlCond(col, Op.GE, expr));
    }

    public SUD ge(ColumnGetter<T, ?> col, Object val, boolean if0) {
        return if0 ? ge(col, val) : $this;
    }

    public SUD ge(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        return if0 ? ge(col, expr) : $this;
    }
    //endregion

    //region 小于 <
    public SUD lt(ColumnGetter<T, ?> col, Object val) {
        return addNode(new SqlCond(col, Op.LT, val));
    }

    public SUD lt(ColumnGetter<T, ?> col, SqlOpExpr expr) {
        return addNode(new SqlCond(col, Op.LT, expr));
    }

    public SUD lt(ColumnGetter<T, ?> col, Object val, boolean if0) {
        return if0 ? lt(col, val) : $this;
    }

    public SUD lt(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        return if0 ? lt(col, expr) : $this;
    }
    //endregion

    //region 小于等于 <=
    public SUD le(ColumnGetter<T, ?> col, Object val) {
        return addNode(new SqlCond(col, Op.LE, val));
    }

    public SUD le(ColumnGetter<T, ?> col, SqlOpExpr expr) {
        return addNode(new SqlCond(col, Op.LE, expr));
    }

    public SUD le(ColumnGetter<T, ?> col, Object val, boolean if0) {
        return if0 ? le(col, val) : $this;
    }

    public SUD le(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        return if0 ? le(col, expr) : $this;
    }
    //endregion

    //region like
    public SUD like(ColumnGetter<T, ?> col, String val) {
        return addNode(new SqlCond(col, Op.LIKE, "%" + val + "%"));
    }

    public SUD like(ColumnGetter<T, ?> col, String val, boolean if0) {
        return if0 ? like(col, val) : $this;
    }
    //endregion

    //region startsWith
    public SUD startsWith(ColumnGetter<T, ?> col, String val) {
        return addNode(new SqlCond(col, Op.LIKE, val + "%"));
    }

    public SUD startsWith(ColumnGetter<T, ?> col, String val, boolean if0) {
        return if0 ? startsWith(col, val) : $this;
    }
    //endregion

    //region endsWith
    public SUD endsWith(ColumnGetter<T, ?> col, String val) {
        return addNode(new SqlCond(col, Op.LIKE, "%" + val));
    }

    public SUD endsWith(ColumnGetter<T, ?> col, String val, boolean if0) {
        return if0 ? endsWith(col, val) : $this;
    }
    //endregion

    //region in
    public SUD in(ColumnGetter<T, ?> col, Object val) {
        return addNode(new SqlCond(col, Op.IN, val));
    }

    public SUD in(ColumnGetter<T, ?> col, Object... vals) {
        return addNode(new SqlCond(col, Op.IN, vals));
    }

    public SUD in(ColumnGetter<T, ?> col, Object val, boolean if0) {
        return if0 ? in(col, val) : $this;
    }

    public SUD in(ColumnGetter<T, ?> col, Collection<Object> vals, boolean if0) {
        return if0 ? in(col, vals) : $this;
    }
    //endregion

    //region not in
    public SUD notIn(ColumnGetter<T, ?> col, Object val) {
        return addNode(new SqlCond(col, Op.IN, val));
    }

    public SUD notIn(ColumnGetter<T, ?> col, Object... vals) {
        return addNode(new SqlCond(col, Op.IN, vals));
    }

    public SUD notIn(ColumnGetter<T, ?> col, Object val, boolean if0) {
        return if0 ? notIn(col, val) : $this;
    }

    public SUD notIn(ColumnGetter<T, ?> col, Collection<Object> vals, boolean if0) {
        return if0 ? notIn(col, vals) : $this;
    }
    //endregion

    //region nulls
    public SUD nulls(ColumnGetter<T, ?> col, Boolean bol) {
        if (bol == null) {
            return $this;
        } else if (bol) {
            return addNode(new SqlCond(col, Op.IS_NULL, null));
        } else {
            return addNode(new SqlCond(col, Op.IS_NOT_NULL, null));
        }
    }
    //endregion

    //region is null
    public SUD isNull(ColumnGetter<T, ?> col) {
        return nulls(col, true);
    }

    public SUD isNull(ColumnGetter<T, ?> col, boolean if0) {
        return nulls(col, if0 ? true : null);
    }
    //endregion

    //region is not null
    public SUD isNotNull(ColumnGetter<T, ?> col) {
        return nulls(col, false);
    }

    public SUD isNotNull(ColumnGetter<T, ?> col, boolean if0) {
        return nulls(col, if0 ? false : null);
    }
    //endregion

    //region between
    public SUD between(ColumnGetter<T, ?> col, Object[] pair) {
        return addNode(new SqlCond(col, Op.BETWEEN, pair));
    }

    public SUD between(ColumnGetter<T, ?> col, Object[] pair, boolean if0) {
        return if0 ? between(col, pair) : $this;
    }

    public SUD between(ColumnGetter<T, ?> col, Object start, Object end) {
        return addNode(new SqlCond(col, Op.BETWEEN, new Object[]{start, end}));
    }

    public SUD between(ColumnGetter<T, ?> col, Object start, Object end, boolean if0) {
        return if0 ? between(col, start, end) : $this;
    }
    //endregion

    //region not between
    public SUD notBetween(ColumnGetter<T, ?> col, Object[] pair) {
        return addNode(new SqlCond(col, Op.NOT_BETWEEN, pair));
    }

    public SUD notBetween(ColumnGetter<T, ?> col, Object[] pair, boolean if0) {
        return if0 ? between(col, pair) : $this;
    }

    public SUD notBetween(ColumnGetter<T, ?> col, Object start, Object end) {
        return addNode(new SqlCond(col, Op.NOT_BETWEEN, new Object[]{start, end}));
    }

    public SUD notBetween(ColumnGetter<T, ?> col, Object a, Object b, boolean if0) {
        return if0 ? between(col, a, b) : $this;
    }
    //endregion
}