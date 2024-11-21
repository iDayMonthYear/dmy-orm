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
        if (if0) {
            return addNode(new Cond(col, Op.EQ, val));
        } else {
            return typedThis;
        }
    }

    public WHERE eq(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        if (if0) {
            return addNode(new Cond(col, Op.EQ, expr));
        } else {
            return typedThis;
        }
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
        if (if0) {
            return addNode(new Cond(col, Op.NE, val));
        } else {
            return typedThis;
        }
    }

    public WHERE ne(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        if (if0) {
            return addNode(new Cond(col, Op.NE, expr));
        } else {
            return typedThis;
        }
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
        if (if0) {
            return addNode(new Cond(col, Op.GT, val));
        } else {
            return typedThis;
        }
    }

    public WHERE gt(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        if (if0) {
            return addNode(new Cond(col, Op.GT, expr));
        } else {
            return typedThis;
        }
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
        if (if0) {
            return addNode(new Cond(col, Op.GE, val));
        } else {
            return typedThis;
        }
    }

    public WHERE ge(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        if (if0) {
            return addNode(new Cond(col, Op.GE, expr));
        } else {
            return typedThis;
        }
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
        if (if0) {
            return addNode(new Cond(col, Op.LT, val));
        } else {
            return typedThis;
        }
    }

    public WHERE lt(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        if (if0) {
            return addNode(new Cond(col, Op.LT, expr));
        } else {
            return typedThis;
        }
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
        if (if0) {
            return addNode(new Cond(col, Op.LE, val));
        } else {
            return typedThis;
        }
    }

    public WHERE le(ColumnGetter<T, ?> col, SqlOpExpr expr, boolean if0) {
        if (if0) {
            return addNode(new Cond(col, Op.LE, expr));
        } else {
            return typedThis;
        }
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
        if (if0) {
            return addNode(new Cond(col, Op.IN, val));
        } else {
            return typedThis;
        }
    }

    public WHERE in(ColumnGetter<T, ?> col, Collection<Object> vals, boolean if0) {
        if (if0) {
            return addNode(new Cond(col, Op.IN, vals));
        } else {
            return typedThis;
        }
    }
    //endregion
}