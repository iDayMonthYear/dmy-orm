package cn.com.idmy.orm.core;

import cn.com.idmy.orm.core.SqlNode.SqlCond;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

import static cn.com.idmy.orm.core.Tables.getColumnName;

@Slf4j
@Accessors(fluent = true, chain = false)
public abstract class Where<T, SUD extends Where<T, SUD>> extends Crud<T, SUD> {
    protected Where(Class<T> entityClass) {
        super(entityClass);
    }

    protected SUD addNode(SqlCond node) {
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
    public SUD eq(FieldGetter<T, ?> field, Object val) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.EQ, val));
    }

    public SUD eq(FieldGetter<T, ?> field, SqlOpExpr expr) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.EQ, expr));
    }

    public SUD eq(FieldGetter<T, ?> field, Object val, boolean if0) {
        return if0 ? eq(field, val) : $this;
    }

    public SUD eq(FieldGetter<T, ?> field, SqlOpExpr expr, boolean if0) {
        return if0 ? eq(field, expr) : $this;
    }
    //endregion

    //region 不等于
    public SUD ne(FieldGetter<T, ?> field, Object val) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.NE, val));
    }

    public SUD ne(FieldGetter<T, ?> field, SqlOpExpr expr) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.NE, expr));
    }

    public SUD ne(FieldGetter<T, ?> field, Object val, boolean if0) {
        return if0 ? ne(field, val) : $this;
    }

    public SUD ne(FieldGetter<T, ?> field, SqlOpExpr expr, boolean if0) {
        return if0 ? ne(field, expr) : $this;
    }
    //endregion

    //region 大于 >
    public SUD gt(FieldGetter<T, ?> field, Object val) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.GT, val));
    }

    public SUD gt(FieldGetter<T, ?> field, SqlOpExpr expr) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.GT, expr));
    }

    public SUD gt(FieldGetter<T, ?> field, Object val, boolean if0) {
        return if0 ? gt(field, val) : $this;
    }

    public SUD gt(FieldGetter<T, ?> field, SqlOpExpr expr, boolean if0) {
        return if0 ? gt(field, expr) : $this;
    }
    //endregion

    //region 大于等于 >=
    public SUD ge(FieldGetter<T, ?> field, Object val) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.GE, val));
    }

    public SUD ge(FieldGetter<T, ?> field, SqlOpExpr expr) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.GE, expr));
    }

    public SUD ge(FieldGetter<T, ?> field, Object val, boolean if0) {
        return if0 ? ge(field, val) : $this;
    }

    public SUD ge(FieldGetter<T, ?> field, SqlOpExpr expr, boolean if0) {
        return if0 ? ge(field, expr) : $this;
    }
    //endregion

    //region 小于 <
    public SUD lt(FieldGetter<T, ?> field, Object val) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.LT, val));
    }

    public SUD lt(FieldGetter<T, ?> field, SqlOpExpr expr) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.LT, expr));
    }

    public SUD lt(FieldGetter<T, ?> field, Object val, boolean if0) {
        return if0 ? lt(field, val) : $this;
    }

    public SUD lt(FieldGetter<T, ?> field, SqlOpExpr expr, boolean if0) {
        return if0 ? lt(field, expr) : $this;
    }
    //endregion

    //region 小于等于 <=
    public SUD le(FieldGetter<T, ?> field, Object val) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.LE, val));
    }

    public SUD le(FieldGetter<T, ?> field, SqlOpExpr expr) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.LE, expr));
    }

    public SUD le(FieldGetter<T, ?> field, Object val, boolean if0) {
        return if0 ? le(field, val) : $this;
    }

    public SUD le(FieldGetter<T, ?> field, SqlOpExpr expr, boolean if0) {
        return if0 ? le(field, expr) : $this;
    }
    //endregion

    //region like
    public SUD like(FieldGetter<T, ?> field, String val) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.LIKE, "%" + val + "%"));
    }

    public SUD like(FieldGetter<T, ?> field, String val, boolean if0) {
        return if0 ? like(field, val) : $this;
    }
    //endregion

    //region startsWith
    public SUD startsWith(FieldGetter<T, ?> field, String val) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.LIKE, val + "%"));
    }

    public SUD startsWith(FieldGetter<T, ?> field, String val, boolean if0) {
        return if0 ? startsWith(field, val) : $this;
    }
    //endregion

    //region endsWith
    public SUD endsWith(FieldGetter<T, ?> field, String val) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.LIKE, "%" + val));
    }

    public SUD endsWith(FieldGetter<T, ?> field, String val, boolean if0) {
        return if0 ? endsWith(field, val) : $this;
    }
    //endregion

    //region in
    public SUD in(FieldGetter<T, ?> field, Object val) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.IN, val));
    }

    public SUD in(FieldGetter<T, ?> field, Object... vals) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.IN, vals));
    }

    public SUD in(FieldGetter<T, ?> field, Object val, boolean if0) {
        return if0 ? in(field, val) : $this;
    }

    public SUD in(FieldGetter<T, ?> field, Collection<Object> vals, boolean if0) {
        return if0 ? in(field, vals) : $this;
    }
    //endregion

    //region not in
    public SUD notIn(FieldGetter<T, ?> field, Object val) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.IN, val));
    }

    public SUD notIn(FieldGetter<T, ?> field, Object... vals) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.IN, vals));
    }

    public SUD notIn(FieldGetter<T, ?> field, Object val, boolean if0) {
        return if0 ? notIn(field, val) : $this;
    }

    public SUD notIn(FieldGetter<T, ?> field, Collection<Object> vals, boolean if0) {
        return if0 ? notIn(field, vals) : $this;
    }
    //endregion

    //region nulls
    public SUD nulls(FieldGetter<T, ?> field, Boolean bol) {
        if (bol == null) {
            return $this;
        } else if (bol) {
            return addNode(new SqlCond(getColumnName(entityClass, field), Op.IS_NULL, null));
        } else {
            return addNode(new SqlCond(getColumnName(entityClass, field), Op.IS_NOT_NULL, null));
        }
    }
    //endregion

    //region is null
    public SUD isNull(FieldGetter<T, ?> field) {
        return nulls(field, true);
    }

    public SUD isNull(FieldGetter<T, ?> field, boolean if0) {
        return nulls(field, if0 ? true : null);
    }
    //endregion

    //region is not null
    public SUD isNotNull(FieldGetter<T, ?> field) {
        return nulls(field, false);
    }

    public SUD isNotNull(FieldGetter<T, ?> field, boolean if0) {
        return nulls(field, if0 ? false : null);
    }
    //endregion

    //region between
    public SUD between(FieldGetter<T, ?> field, Object[] pair) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.BETWEEN, pair));
    }

    public SUD between(FieldGetter<T, ?> field, Object[] pair, boolean if0) {
        return if0 ? between(field, pair) : $this;
    }

    public SUD between(FieldGetter<T, ?> field, Object start, Object end) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.BETWEEN, new Object[]{start, end}));
    }

    public SUD between(FieldGetter<T, ?> field, Object start, Object end, boolean if0) {
        return if0 ? between(field, start, end) : $this;
    }
    //endregion

    //region not between
    public SUD notBetween(FieldGetter<T, ?> field, Object[] pair) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.NOT_BETWEEN, pair));
    }

    public SUD notBetween(FieldGetter<T, ?> field, Object[] pair, boolean if0) {
        return if0 ? between(field, pair) : $this;
    }

    public SUD notBetween(FieldGetter<T, ?> field, Object start, Object end) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.NOT_BETWEEN, new Object[]{start, end}));
    }

    public SUD notBetween(FieldGetter<T, ?> field, Object a, Object b, boolean if0) {
        return if0 ? between(field, a, b) : $this;
    }
    //endregion
}