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
    public WHERE eq(FieldGetter<T, ?> field, Object value) {
        return addNode(new Cond(field, Op.EQ, value));
    }

    public WHERE eq(FieldGetter<T, ?> field, SqlOpExpr expr) {
        return addNode(new Cond(field, Op.EQ, expr));
    }

    public WHERE eq(FieldGetter<T, ?> field, Object value, boolean if0) {
        if (if0) {
            return addNode(new Cond(field, Op.EQ, value));
        } else {
            return typedThis;
        }
    }

    public WHERE eq(FieldGetter<T, ?> field, SqlOpExpr expr, boolean if0) {
        if (if0) {
            return addNode(new Cond(field, Op.EQ, expr));
        } else {
            return typedThis;
        }
    }
    //endregion

    //region 不等于
    public WHERE ne(FieldGetter<T, ?> field, Object value) {
        return addNode(new Cond(field, Op.NE, value));
    }

    public WHERE ne(FieldGetter<T, ?> field, SqlOpExpr expr) {
        return addNode(new Cond(field, Op.NE, expr));
    }

    public WHERE ne(FieldGetter<T, ?> field, Object value, boolean if0) {
        if (if0) {
            return addNode(new Cond(field, Op.NE, value));
        } else {
            return typedThis;
        }
    }

    public WHERE ne(FieldGetter<T, ?> field, SqlOpExpr expr, boolean if0) {
        if (if0) {
            return addNode(new Cond(field, Op.NE, expr));
        } else {
            return typedThis;
        }
    }
    //endregion

    //region 大于 >
    public WHERE gt(FieldGetter<T, ?> field, Object value) {
        return addNode(new Cond(field, Op.GT, value));
    }

    public WHERE gt(FieldGetter<T, ?> field, SqlOpExpr expr) {
        return addNode(new Cond(field, Op.GT, expr));
    }

    public WHERE gt(FieldGetter<T, ?> field, Object value, boolean if0) {
        if (if0) {
            return addNode(new Cond(field, Op.GT, value));
        } else {
            return typedThis;
        }
    }

    public WHERE gt(FieldGetter<T, ?> field, SqlOpExpr expr, boolean if0) {
        if (if0) {
            return addNode(new Cond(field, Op.GT, expr));
        } else {
            return typedThis;
        }
    }
    //endregion

    //region 大于等于 >=
    public WHERE ge(FieldGetter<T, ?> field, Object value) {
        return addNode(new Cond(field, Op.GE, value));
    }

    public WHERE ge(FieldGetter<T, ?> field, SqlOpExpr expr) {
        return addNode(new Cond(field, Op.GE, expr));
    }

    public WHERE ge(FieldGetter<T, ?> field, Object value, boolean if0) {
        if (if0) {
            return addNode(new Cond(field, Op.GE, value));
        } else {
            return typedThis;
        }
    }

    public WHERE ge(FieldGetter<T, ?> field, SqlOpExpr expr, boolean if0) {
        if (if0) {
            return addNode(new Cond(field, Op.GE, expr));
        } else {
            return typedThis;
        }
    }
    //endregion

    //region 小于 <
    public WHERE lt(FieldGetter<T, ?> field, Object value) {
        return addNode(new Cond(field, Op.LT, value));
    }

    public WHERE lt(FieldGetter<T, ?> field, SqlOpExpr expr) {
        return addNode(new Cond(field, Op.LT, expr));
    }

    public WHERE lt(FieldGetter<T, ?> field, Object value, boolean if0) {
        if (if0) {
            return addNode(new Cond(field, Op.LT, value));
        } else {
            return typedThis;
        }
    }

    public WHERE lt(FieldGetter<T, ?> field, SqlOpExpr expr, boolean if0) {
        if (if0) {
            return addNode(new Cond(field, Op.LT, expr));
        } else {
            return typedThis;
        }
    }
    //endregion

    //region 小于等于 <=
    public WHERE le(FieldGetter<T, ?> field, Object value) {
        return addNode(new Cond(field, Op.LE, value));
    }

    public WHERE le(FieldGetter<T, ?> field, SqlOpExpr expr) {
        return addNode(new Cond(field, Op.LE, expr));
    }

    public WHERE le(FieldGetter<T, ?> field, Object value, boolean if0) {
        if (if0) {
            return addNode(new Cond(field, Op.LE, value));
        } else {
            return typedThis;
        }
    }

    public WHERE le(FieldGetter<T, ?> field, SqlOpExpr expr, boolean if0) {
        if (if0) {
            return addNode(new Cond(field, Op.LE, expr));
        } else {
            return typedThis;
        }
    }
    //endregion

    //region in
    public WHERE in(FieldGetter<T, ?> field, Object value) {
        return addNode(new Cond(field, Op.IN, value));
    }

    public WHERE in(FieldGetter<T, ?> field, Object... values) {
        return addNode(new Cond(field, Op.IN, values));
    }

    public WHERE in(FieldGetter<T, ?> field, Object value, boolean if0) {
        if (if0) {
            return addNode(new Cond(field, Op.IN, value));
        } else {
            return typedThis;
        }
    }

    public WHERE in(FieldGetter<T, ?> field, Collection<Object> values, boolean if0) {
        if (if0) {
            return addNode(new Cond(field, Op.IN, values));
        } else {
            return typedThis;
        }
    }
    //endregion
}