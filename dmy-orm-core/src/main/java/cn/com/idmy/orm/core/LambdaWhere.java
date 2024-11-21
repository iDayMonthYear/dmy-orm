package cn.com.idmy.orm.core;

import cn.com.idmy.orm.core.Node.Cond;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@Slf4j
@Getter
@Accessors(fluent = true, chain = false)
public abstract class LambdaWhere<T, WHERE extends LambdaWhere<T, WHERE>> extends AbstractWhere<T, WHERE> {
    protected LambdaWhere(Class<T> entityClass) {
        super(entityClass);
    }

    //region 等于
    public WHERE eq(ColumnGetter<T, ?> col, Object val) {
        return addNode(new Cond(col, Op.EQ, val));
    }

    public WHERE eq(ColumnGetter<T, ?> col, SqlOpExpr expr) {
        return addNode(new Cond(col, Op.EQ, expr));
    }

    public WHERE eq(ColumnGetter<T, ?> col, Object val, boolean if0) {
        return if0 ? addNode(new Cond(col, Op.EQ, val)) : $this;
    }

    public WHERE eq(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        return if0 ? addNode(new Cond(col, Op.EQ, expr)) : $this;
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
        return if0 ? addNode(new Cond(col, Op.NE, val)) : $this;
    }

    public WHERE ne(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        return if0 ? addNode(new Cond(col, Op.NE, expr)) : $this;
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
        return if0 ? addNode(new Cond(col, Op.GT, val)) : $this;
    }

    public WHERE gt(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        return if0 ? addNode(new Cond(col, Op.GT, expr)) : $this;
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
        return if0 ? addNode(new Cond(col, Op.GE, val)) : $this;
    }

    public WHERE ge(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        return if0 ? addNode(new Cond(col, Op.GE, expr)) : $this;
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
        return if0 ? addNode(new Cond(col, Op.LT, val)) : $this;
    }

    public WHERE lt(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        return if0 ? addNode(new Cond(col, Op.LT, expr)) : $this;
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
        return if0 ? addNode(new Cond(col, Op.LE, val)) : $this;
    }

    public WHERE le(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        return if0 ? addNode(new Cond(col, Op.LE, expr)) : $this;
    }
    //endregion

    //region like
    public WHERE like(ColumnGetter<T, ?> col, String val) {
        return addNode(new Cond(col, Op.LIKE, val));
    }

    public WHERE like(ColumnGetter<T, ?> col, String val, boolean if0) {
        return if0 ? addNode(new Cond(col, Op.LIKE, val)) : $this;
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
        return if0 ? addNode(new Cond(col, Op.IN, val)) : $this;
    }

    public WHERE in(ColumnGetter<T, ?> col, Collection<Object> vals, boolean if0) {
        return if0 ? addNode(new Cond(col, Op.IN, vals)) : $this;
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
    public WHERE between(ColumnGetter<T, ?> col, Object[] vals) {
        return addNode(new Cond(col, Op.BETWEEN, vals));
    }

    public WHERE between(ColumnGetter<T, ?> col, Object[] vals, boolean if0) {
        return if0 ? addNode(new Cond(col, Op.BETWEEN, vals)) : $this;
    }
    //endregion
}