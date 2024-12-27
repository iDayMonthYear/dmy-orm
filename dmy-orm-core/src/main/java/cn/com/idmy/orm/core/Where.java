package cn.com.idmy.orm.core;

import cn.com.idmy.orm.core.SqlNode.SqlCond;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static cn.com.idmy.orm.core.Tables.getColumnName;

@Slf4j
@Accessors(fluent = true, chain = false)
public abstract class Where<T, SUD extends Where<T, SUD>> extends Crud<T, SUD> {
    protected Where(@NotNull Class<T> entityClass) {
        super(entityClass);
    }

    @NotNull
    protected SUD addNode(@NotNull SqlCond node) {
        switch (node.expr) {
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
    @NotNull
    public <V> SUD eq(@NotNull FieldGetter<T, V> field, @Nullable V val) {
        return val == null ? $this : addNode(new SqlCond(getColumnName(entityClass, field), Op.EQ, val));
    }

    @NotNull
    public <V> SUD eq(@NotNull FieldGetter<T, V> field, @NotNull SqlOpExpr expr) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.EQ, expr));
    }

    @NotNull
    public <V> SUD eq(@NotNull FieldGetter<T, V> field, @Nullable V val, boolean if0) {
        return if0 ? eq(field, val) : $this;
    }

    @NotNull
    public <V> SUD eq(@NotNull FieldGetter<T, V> field, @NotNull SqlOpExpr expr, boolean if0) {
        return if0 ? eq(field, expr) : $this;
    }
    //endregion

    //region 不等于
    @NotNull
    public <V> SUD ne(@NotNull FieldGetter<T, V> field, @Nullable V val) {
        return val == null ? $this : addNode(new SqlCond(getColumnName(entityClass, field), Op.NE, val));
    }

    @NotNull
    public <V> SUD ne(@NotNull FieldGetter<T, V> field, @NotNull SqlOpExpr expr) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.NE, expr));
    }

    @NotNull
    public <V> SUD ne(@NotNull FieldGetter<T, V> field, @Nullable V val, boolean if0) {
        return if0 ? ne(field, val) : $this;
    }

    @NotNull
    public <V> SUD ne(@NotNull FieldGetter<T, V> field, @NotNull SqlOpExpr expr, boolean if0) {
        return if0 ? ne(field, expr) : $this;
    }
    //endregion

    //region 大于 >
    @NotNull
    public <V> SUD gt(@NotNull FieldGetter<T, V> field, @Nullable V val) {
        return val == null ? $this : addNode(new SqlCond(getColumnName(entityClass, field), Op.GT, val));
    }

    @NotNull
    public <V> SUD gt(@NotNull FieldGetter<T, V> field, @NotNull SqlOpExpr expr) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.GT, expr));
    }

    @NotNull
    public <V> SUD gt(@NotNull FieldGetter<T, V> field, @Nullable V val, boolean if0) {
        return if0 ? gt(field, val) : $this;
    }

    @NotNull
    public <V> SUD gt(@NotNull FieldGetter<T, V> field, @NotNull SqlOpExpr expr, boolean if0) {
        return if0 ? gt(field, expr) : $this;
    }
    //endregion

    //region 大于等于 >=
    @NotNull
    public <V> SUD ge(@NotNull FieldGetter<T, V> field, @Nullable V val) {
        return val == null ? $this : addNode(new SqlCond(getColumnName(entityClass, field), Op.GE, val));
    }

    @NotNull
    public <V> SUD ge(@NotNull FieldGetter<T, V> field, @NotNull SqlOpExpr expr) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.GE, expr));
    }

    @NotNull
    public <V> SUD ge(@NotNull FieldGetter<T, V> field, @Nullable V val, boolean if0) {
        return if0 ? ge(field, val) : $this;
    }

    @NotNull
    public <V> SUD ge(@NotNull FieldGetter<T, V> field, @NotNull SqlOpExpr expr, boolean if0) {
        return if0 ? ge(field, expr) : $this;
    }
    //endregion

    //region 小于 <
    @NotNull
    public <V> SUD lt(@NotNull FieldGetter<T, V> field, @Nullable V val) {
        return val == null ? $this : addNode(new SqlCond(getColumnName(entityClass, field), Op.LT, val));
    }

    @NotNull
    public <V> SUD lt(@NotNull FieldGetter<T, V> field, @NotNull SqlOpExpr expr) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.LT, expr));
    }

    @NotNull
    public <V> SUD lt(@NotNull FieldGetter<T, V> field, @Nullable V val, boolean if0) {
        return if0 ? lt(field, val) : $this;
    }

    @NotNull
    public <V> SUD lt(@NotNull FieldGetter<T, V> field, SqlOpExpr expr, boolean if0) {
        return if0 ? lt(field, expr) : $this;
    }
    //endregion

    //region 小于等于 <=
    @NotNull
    public <V> SUD le(@NotNull FieldGetter<T, V> field, @Nullable V val) {
        return val == null ? $this : addNode(new SqlCond(getColumnName(entityClass, field), Op.LE, val));
    }

    @NotNull
    public <V> SUD le(@NotNull FieldGetter<T, V> field, SqlOpExpr expr) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.LE, expr));
    }

    @NotNull
    public <V> SUD le(@NotNull FieldGetter<T, V> field, @Nullable V val, boolean if0) {
        return if0 ? le(field, val) : $this;
    }

    @NotNull
    public <V> SUD le(@NotNull FieldGetter<T, V> field, SqlOpExpr expr, boolean if0) {
        return if0 ? le(field, expr) : $this;
    }
    //endregion

    //region like
    @NotNull
    public SUD like(@NotNull FieldGetter<T, String> field, @Nullable String val) {
        return StrUtil.isBlank(val) ? $this : addNode(new SqlCond(getColumnName(entityClass, field), Op.LIKE, "%" + val + "%"));
    }

    @NotNull
    public SUD like(@NotNull FieldGetter<T, String> field, @Nullable String val, boolean if0) {
        return if0 ? like(field, val) : $this;
    }
    //endregion

    //region startsWith
    @NotNull
    public SUD startsWith(@NotNull FieldGetter<T, String> field, @Nullable String val) {
        return StrUtil.isBlank(val) ? $this : addNode(new SqlCond(getColumnName(entityClass, field), Op.LIKE, val + "%"));
    }

    @NotNull
    public SUD startsWith(@NotNull FieldGetter<T, String> field, @Nullable String val, boolean if0) {
        return if0 ? startsWith(field, val) : $this;
    }
    //endregion

    //region endsWith
    @NotNull
    public SUD endsWith(@NotNull FieldGetter<T, String> field, @Nullable String val) {
        return StrUtil.isBlank(val) ? $this : addNode(new SqlCond(getColumnName(entityClass, field), Op.LIKE, "%" + val));
    }

    @NotNull
    public SUD endsWith(@NotNull FieldGetter<T, String> field, @Nullable String val, boolean if0) {
        return if0 ? endsWith(field, val) : $this;
    }
    //endregion

    //region in
    @NotNull
    public <V> SUD in(@NotNull FieldGetter<T, V> field, @Nullable V val) {
        return val == null ? $this : addNode(new SqlCond(getColumnName(entityClass, field), Op.IN, val));
    }

    @NotNull
    public <V> SUD in(@NotNull FieldGetter<T, V> field, @Nullable V... vals) {
        return ArrayUtil.isEmpty(vals) ? $this : addNode(new SqlCond(getColumnName(entityClass, field), Op.IN, vals));
    }

    @NotNull
    public <V> SUD in(@NotNull FieldGetter<T, V> field, @Nullable Collection<V> vals) {
        return CollUtil.isEmpty(vals) ? $this : addNode(new SqlCond(getColumnName(entityClass, field), Op.IN, vals));
    }

    @NotNull
    public <V> SUD in(@NotNull FieldGetter<T, V> field, @Nullable V val, boolean if0) {
        return if0 ? in(field, val) : $this;
    }

    @NotNull
    public <V> SUD in(@NotNull FieldGetter<T, V> field, @Nullable Collection<V> vals, boolean if0) {
        return if0 ? in(field, vals) : $this;
    }

    @NotNull
    public <V> SUD in(@NotNull FieldGetter<T, V> field, @Nullable V[] vals, boolean if0) {
        return if0 ? in(field, vals) : $this;
    }
    //endregion

    //region not in
    @NotNull
    public <V> SUD notIn(@NotNull FieldGetter<T, V> field, @Nullable V val) {
        return val == null ? $this : addNode(new SqlCond(getColumnName(entityClass, field), Op.NOT_IN, val));
    }

    @NotNull
    public <V> SUD notIn(@NotNull FieldGetter<T, V> field, @Nullable V... vals) {
        return ArrayUtil.isEmpty(vals) ? $this : addNode(new SqlCond(getColumnName(entityClass, field), Op.NOT_IN, vals));
    }

    @NotNull
    public <V> SUD notIn(@NotNull FieldGetter<T, V> field, @Nullable Collection<V> vals) {
        return CollUtil.isEmpty(vals) ? $this : addNode(new SqlCond(getColumnName(entityClass, field), Op.NOT_IN, vals));
    }

    @NotNull
    public <V> SUD notIn(@NotNull FieldGetter<T, V> field, @Nullable V val, boolean if0) {
        return if0 ? in(field, val) : $this;
    }

    @NotNull
    public <V> SUD notIn(@NotNull FieldGetter<T, V> field, @Nullable Collection<V> vals, boolean if0) {
        return if0 ? in(field, vals) : $this;
    }

    @NotNull
    public <V> SUD notIn(@NotNull FieldGetter<T, V> field, @Nullable V[] vals, boolean if0) {
        return if0 ? in(field, vals) : $this;
    }

    //endregion

    //region nulls
    @NotNull
    public SUD nulls(@NotNull FieldGetter<T, ?> field, @Nullable Boolean bol) {
        if (bol == null) {
            return $this;
        } else if (bol) {
            return addNode(new SqlCond(getColumnName(entityClass, field), Op.IS_NULL, new Object()));
        } else {
            return addNode(new SqlCond(getColumnName(entityClass, field), Op.IS_NOT_NULL, new Object()));
        }
    }
    //endregion

    //region is null
    @NotNull
    public SUD isNull(@NotNull FieldGetter<T, ?> field) {
        return nulls(field, true);
    }

    @NotNull
    public SUD isNull(@NotNull FieldGetter<T, ?> field, boolean if0) {
        return nulls(field, if0 ? true : null);
    }
    //endregion

    //region is not null
    @NotNull
    public SUD isNotNull(@NotNull FieldGetter<T, ?> field) {
        return nulls(field, false);
    }

    @NotNull
    public SUD isNotNull(@NotNull FieldGetter<T, ?> field, boolean if0) {
        return nulls(field, if0 ? false : null);
    }
    //endregion

    //region between
    @NotNull
    public <V> SUD between(@NotNull FieldGetter<T, V> field, @Nullable V[] pair) {
        if (pair == null || pair.length != 2) {
            return $this;
        } else {
            return addNode(new SqlCond(getColumnName(entityClass, field), Op.BETWEEN, pair));
        }
    }

    @NotNull
    public <V> SUD between(@NotNull FieldGetter<T, V> field, @Nullable V[] pair, boolean if0) {
        return if0 ? between(field, pair) : $this;
    }

    @NotNull
    public <V> SUD between(@NotNull FieldGetter<T, V> field, @Nullable V start, @Nullable V end) {
        if (start == null || end == null) {
            return $this;
        } else {
            return addNode(new SqlCond(getColumnName(entityClass, field), Op.BETWEEN, new Object[]{start, end}));
        }
    }

    @NotNull
    public <V> SUD between(@NotNull FieldGetter<T, V> field, @Nullable V start, @Nullable V end, boolean if0) {
        return if0 ? between(field, start, end) : $this;
    }
    //endregion

    //region not between
    @NotNull
    public <V> SUD notBetween(@NotNull FieldGetter<T, V> field, @Nullable V[] pair) {
        if (pair == null || pair.length != 2) {
            return $this;
        } else {
            return addNode(new SqlCond(getColumnName(entityClass, field), Op.NOT_BETWEEN, pair));
        }
    }

    @NotNull
    public <V> SUD notBetween(@NotNull FieldGetter<T, V> field, @Nullable V[] pair, boolean if0) {
        return if0 ? between(field, pair) : $this;
    }

    @NotNull
    public <V> SUD notBetween(@NotNull FieldGetter<T, V> field, @Nullable V start, @Nullable V end) {
        if (start == null || end == null) {
            return $this;
        } else {
            return addNode(new SqlCond(getColumnName(entityClass, field), Op.NOT_BETWEEN, new Object[]{start, end}));
        }
    }

    @NotNull
    public <V> SUD notBetween(@NotNull FieldGetter<T, V> field, @Nullable V start, @Nullable V end, boolean if0) {
        return if0 ? between(field, start, end) : $this;
    }
    //endregion
}