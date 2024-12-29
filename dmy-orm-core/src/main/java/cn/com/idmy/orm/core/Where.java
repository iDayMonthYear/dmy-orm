package cn.com.idmy.orm.core;

import cn.com.idmy.orm.core.SqlNode.SqlCond;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
                    return crud;
                }
            }
            case Object[] arr -> {
                if (arr.length == 0) {
                    return crud;
                }
            }
            default -> {
            }
        }
        return super.addNode(node);
    }

    //region 比较操作
    // 等于
    @NotNull
    public SUD eq(@NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return val == null ? crud : addNode(new SqlCond(getColumnName(entityClass, field), Op.EQ, val));
    }

    @NotNull
    public SUD eq(@NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.EQ, expr));
    }

    @NotNull
    public SUD eq(@NotNull FieldGetter<T, ?> field, @Nullable Object val, boolean if0) {
        return if0 ? eq(field, val) : crud;
    }

    @NotNull
    public SUD eq(@NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr, boolean if0) {
        return if0 ? eq(field, expr) : crud;
    }

    // 不等于
    @NotNull
    public SUD ne(@NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return val == null ? crud : addNode(new SqlCond(getColumnName(entityClass, field), Op.NE, val));
    }

    @NotNull
    public SUD ne(@NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.NE, expr));
    }

    @NotNull
    public SUD ne(@NotNull FieldGetter<T, ?> field, @Nullable Object val, boolean if0) {
        return if0 ? ne(field, val) : crud;
    }

    @NotNull
    public SUD ne(@NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr, boolean if0) {
        return if0 ? ne(field, expr) : crud;
    }

    // 大于
    @NotNull
    public SUD gt(@NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return val == null ? crud : addNode(new SqlCond(getColumnName(entityClass, field), Op.GT, val));
    }

    @NotNull
    public SUD gt(@NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.GT, expr));
    }

    @NotNull
    public SUD gt(@NotNull FieldGetter<T, ?> field, @Nullable Object val, boolean if0) {
        return if0 ? gt(field, val) : crud;
    }

    @NotNull
    public SUD gt(@NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr, boolean if0) {
        return if0 ? gt(field, expr) : crud;
    }

    // 大于等于
    @NotNull
    public SUD ge(@NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return val == null ? crud : addNode(new SqlCond(getColumnName(entityClass, field), Op.GE, val));
    }

    @NotNull
    public SUD ge(@NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.GE, expr));
    }

    @NotNull
    public SUD ge(@NotNull FieldGetter<T, ?> field, @Nullable Object val, boolean if0) {
        return if0 ? ge(field, val) : crud;
    }

    @NotNull
    public SUD ge(@NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr, boolean if0) {
        return if0 ? ge(field, expr) : crud;
    }

    // 小于
    @NotNull
    public SUD lt(@NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return val == null ? crud : addNode(new SqlCond(getColumnName(entityClass, field), Op.LT, val));
    }

    @NotNull
    public SUD lt(@NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.LT, expr));
    }

    @NotNull
    public SUD lt(@NotNull FieldGetter<T, ?> field, @Nullable Object val, boolean if0) {
        return if0 ? lt(field, val) : crud;
    }

    @NotNull
    public SUD lt(@NotNull FieldGetter<T, ?> field, SqlOpExpr expr, boolean if0) {
        return if0 ? lt(field, expr) : crud;
    }

    // 小于等于
    @NotNull
    public SUD le(@NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return val == null ? crud : addNode(new SqlCond(getColumnName(entityClass, field), Op.LE, val));
    }

    @NotNull
    public SUD le(@NotNull FieldGetter<T, ?> field, SqlOpExpr expr) {
        return addNode(new SqlCond(getColumnName(entityClass, field), Op.LE, expr));
    }

    @NotNull
    public SUD le(@NotNull FieldGetter<T, ?> field, @Nullable Object val, boolean if0) {
        return if0 ? le(field, val) : crud;
    }

    @NotNull
    public SUD le(@NotNull FieldGetter<T, ?> field, SqlOpExpr expr, boolean if0) {
        return if0 ? le(field, expr) : crud;
    }
    //endregion

    //region 字符串操作

    @NotNull
    public SUD like(@NotNull FieldGetter<T, String> field, @Nullable String val) {
        return StrUtil.isBlank(val) ? crud : addNode(new SqlCond(getColumnName(entityClass, field), Op.LIKE, "%" + val + "%"));
    }

    @NotNull
    public SUD like(@NotNull FieldGetter<T, String> field, @Nullable String val, boolean if0) {
        return if0 ? like(field, val) : crud;
    }

    @NotNull
    public SUD startsWith(@NotNull FieldGetter<T, String> field, @Nullable String val) {
        return StrUtil.isBlank(val) ? crud : addNode(new SqlCond(getColumnName(entityClass, field), Op.LIKE, val + "%"));
    }

    @NotNull
    public SUD startsWith(@NotNull FieldGetter<T, String> field, @Nullable String val, boolean if0) {
        return if0 ? startsWith(field, val) : crud;
    }

    @NotNull
    public SUD endsWith(@NotNull FieldGetter<T, String> field, @Nullable String val) {
        return StrUtil.isBlank(val) ? crud : addNode(new SqlCond(getColumnName(entityClass, field), Op.LIKE, "%" + val));
    }

    @NotNull
    public SUD endsWith(@NotNull FieldGetter<T, String> field, @Nullable String val, boolean if0) {
        return if0 ? endsWith(field, val) : crud;
    }

    @NotNull
    public SUD notLike(@NotNull FieldGetter<T, String> field, @Nullable String val) {
        return StrUtil.isBlank(val) ? crud : addNode(new SqlCond(getColumnName(entityClass, field), Op.NOT_LIKE, "%" + val + "%"));
    }

    @NotNull
    public SUD notLike(@NotNull FieldGetter<T, String> field, @Nullable String val, boolean if0) {
        return if0 ? notLike(field, val) : crud;
    }

    @NotNull
    public SUD notStartsWith(@NotNull FieldGetter<T, String> field, @Nullable String val) {
        return StrUtil.isBlank(val) ? crud : addNode(new SqlCond(getColumnName(entityClass, field), Op.NOT_LIKE, val + "%"));
    }

    @NotNull
    public SUD notStartsWith(@NotNull FieldGetter<T, String> field, @Nullable String val, boolean if0) {
        return if0 ? notStartsWith(field, val) : crud;
    }

    @NotNull
    public SUD notEndsWith(@NotNull FieldGetter<T, String> field, @Nullable String val) {
        return StrUtil.isBlank(val) ? crud : addNode(new SqlCond(getColumnName(entityClass, field), Op.NOT_LIKE, "%" + val));
    }

    @NotNull
    public SUD notEndsWith(@NotNull FieldGetter<T, String> field, @Nullable String val, boolean if0) {
        return if0 ? notEndsWith(field, val) : crud;
    }
    //endregion

    //region 包含操作
    // IN
    @NotNull
    public SUD in(@NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return val == null ? crud : addNode(new SqlCond(getColumnName(entityClass, field), Op.IN, val));
    }

    @NotNull
    public SUD in(@NotNull FieldGetter<T, ?> field, @Nullable Object... vals) {
        return ArrayUtil.isEmpty(vals) ? crud : addNode(new SqlCond(getColumnName(entityClass, field), Op.IN, vals));
    }

    @NotNull
    public SUD in(@NotNull FieldGetter<T, ?> field, @Nullable Collection<?> vals) {
        return CollUtil.isEmpty(vals) ? crud : addNode(new SqlCond(getColumnName(entityClass, field), Op.IN, vals));
    }

    @NotNull
    public SUD in(@NotNull FieldGetter<T, ?> field, @Nullable Object val, boolean if0) {
        return if0 ? in(field, val) : crud;
    }

    @NotNull
    public SUD in(@NotNull FieldGetter<T, ?> field, @Nullable Collection<?> vals, boolean if0) {
        return if0 ? in(field, vals) : crud;
    }

    @NotNull
    public SUD in(@NotNull FieldGetter<T, ?> field, @Nullable Object[] vals, boolean if0) {
        return if0 ? in(field, vals) : crud;
    }

    // NOT IN
    @NotNull
    public SUD notIn(@NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return val == null ? crud : addNode(new SqlCond(getColumnName(entityClass, field), Op.NOT_IN, val));
    }

    @NotNull
    public SUD notIn(@NotNull FieldGetter<T, ?> field, @Nullable Object... vals) {
        return ArrayUtil.isEmpty(vals) ? crud : addNode(new SqlCond(getColumnName(entityClass, field), Op.NOT_IN, vals));
    }

    @NotNull
    public SUD notIn(@NotNull FieldGetter<T, ?> field, @Nullable Collection<?> vals) {
        return CollUtil.isEmpty(vals) ? crud : addNode(new SqlCond(getColumnName(entityClass, field), Op.NOT_IN, vals));
    }

    @NotNull
    public SUD notIn(@NotNull FieldGetter<T, ?> field, @Nullable Object val, boolean if0) {
        return if0 ? in(field, val) : crud;
    }

    @NotNull
    public SUD notIn(@NotNull FieldGetter<T, ?> field, @Nullable Collection<?> vals, boolean if0) {
        return if0 ? in(field, vals) : crud;
    }

    @NotNull
    public SUD notIn(@NotNull FieldGetter<T, ?> field, @Nullable Object[] vals, boolean if0) {
        return if0 ? in(field, vals) : crud;
    }
    //endregion

    //region NULL值操作
    @NotNull
    public SUD nulls(@NotNull FieldGetter<T, ?> field, @Nullable Boolean bol) {
        if (bol == null) {
            return crud;
        } else if (bol) {
            return addNode(new SqlCond(getColumnName(entityClass, field), Op.IS_NULL, true));
        } else {
            return addNode(new SqlCond(getColumnName(entityClass, field), Op.IS_NOT_NULL, true));
        }
    }

    @NotNull
    public SUD isNull(@NotNull FieldGetter<T, ?> field) {
        return nulls(field, true);
    }

    @NotNull
    public SUD isNull(@NotNull FieldGetter<T, ?> field, boolean if0) {
        return nulls(field, if0 ? true : null);
    }

    @NotNull
    public SUD isNotNull(@NotNull FieldGetter<T, ?> field) {
        return nulls(field, false);
    }

    @NotNull
    public SUD isNotNull(@NotNull FieldGetter<T, ?> field, boolean if0) {
        return nulls(field, if0 ? false : null);
    }
    //endregion

    //region 范围操作
    // BETWEEN - 通用类型
    @NotNull
    public SUD between(@NotNull FieldGetter<T, ?> field, @Nullable Object[] pair) {
        if (pair == null || pair.length != 2) {
            return crud;
        } else {
            return addNode(new SqlCond(getColumnName(entityClass, field), Op.BETWEEN, pair));
        }
    }

    @NotNull
    public SUD between(@NotNull FieldGetter<T, ?> field, @Nullable Object start, @Nullable Object end) {
        if (start == null || end == null) {
            return crud;
        } else {
            return addNode(new SqlCond(getColumnName(entityClass, field), Op.BETWEEN, new Object[]{start, end}));
        }
    }

    @NotNull
    public SUD between(@NotNull FieldGetter<T, ?> field, @Nullable Object[] pair, boolean if0) {
        return if0 ? between(field, pair) : crud;
    }

    @NotNull
    public SUD between(@NotNull FieldGetter<T, ?> field, @Nullable Object start, @Nullable Object end, boolean if0) {
        return if0 ? between(field, start, end) : crud;
    }

    // BETWEEN - LocalDateTime
    @NotNull
    public SUD between(@NotNull FieldGetter<T, LocalDateTime> field, @Nullable LocalDateTime start, @Nullable LocalDateTime end) {
        if (start == null && end == null) {
            return crud;
        }
        var a = start;
        var b = end;
        if (start == null) {
            a = LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0);
        }
        if (end == null) {
            b = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999_999_999);
        }
        if (a.toLocalDate().equals(b.toLocalDate()) && a.getHour() == b.getHour() && a.getMinute() == b.getMinute() && a.getSecond() == b.getSecond() && a.getNano() == b.getNano()) {
            var dayStart = a.toLocalDate().atStartOfDay();
            var dayEnd = a.toLocalDate().atTime(23, 59, 59, 999_999_999);
            return between(field, new LocalDateTime[]{dayStart, dayEnd});
        } else {
            return between(field, new LocalDateTime[]{a, b});
        }
    }

    @NotNull
    public SUD between(@NotNull FieldGetter<T, LocalDateTime> field, @Nullable LocalDateTime start, @Nullable LocalDateTime end, boolean if0) {
        return if0 ? between(field, start, end) : crud;
    }

    // BETWEEN - LocalDate
    @NotNull
    public SUD between(@NotNull FieldGetter<T, LocalDate> field, @Nullable LocalDate start, @Nullable LocalDate end) {
        if (start == null && end == null) {
            return crud;
        }
        var a = start;
        var b = end;
        if (start == null) {
            a = LocalDate.of(1970, 1, 1);
        }
        if (end == null) {
            b = LocalDate.now();
        }
        return between(field, new LocalDate[]{a, b});
    }

    @NotNull
    public SUD between(@NotNull FieldGetter<T, LocalDate> field, @Nullable LocalDate start, @Nullable LocalDate end, boolean if0) {
        return if0 ? between(field, start, end) : crud;
    }

    // BETWEEN - LocalTime
    @NotNull
    public SUD between(@NotNull FieldGetter<T, LocalTime> field, @Nullable LocalTime start, @Nullable LocalTime end) {
        if (start == null && end == null) {
            return crud;
        }
        var a = start;
        var b = end;
        if (start == null) {
            a = LocalTime.MIN;
        }
        if (end == null) {
            b = LocalTime.MAX;
        }
        if (a.equals(b)) {
            return between(field, new LocalTime[]{LocalTime.MIN, LocalTime.MAX});
        } else {
            return between(field, new LocalTime[]{a, b});
        }
    }

    @NotNull
    public SUD between(@NotNull FieldGetter<T, LocalTime> field, @Nullable LocalTime start, @Nullable LocalTime end, boolean if0) {
        return if0 ? between(field, start, end) : crud;
    }

    // NOT BETWEEN - 通用类型
    @NotNull
    public SUD notBetween(@NotNull FieldGetter<T, ?> field, @Nullable Object[] pair) {
        if (pair == null || pair.length != 2) {
            return crud;
        } else {
            return addNode(new SqlCond(getColumnName(entityClass, field), Op.NOT_BETWEEN, pair));
        }
    }

    @NotNull
    public SUD notBetween(@NotNull FieldGetter<T, ?> field, @Nullable Object start, @Nullable Object end) {
        if (start == null || end == null) {
            return crud;
        } else {
            return addNode(new SqlCond(getColumnName(entityClass, field), Op.NOT_BETWEEN, new Object[]{start, end}));
        }
    }

    @NotNull
    public SUD notBetween(@NotNull FieldGetter<T, ?> field, @Nullable Object[] pair, boolean if0) {
        return if0 ? between(field, pair) : crud;
    }

    @NotNull
    public SUD notBetween(@NotNull FieldGetter<T, ?> field, @Nullable Object start, @Nullable Object end, boolean if0) {
        return if0 ? between(field, start, end) : crud;
    }

    // NOT BETWEEN - LocalDateTime
    @NotNull
    public SUD notBetween(@NotNull FieldGetter<T, LocalDateTime> field, @Nullable LocalDateTime start, @Nullable LocalDateTime end) {
        if (start == null && end == null) {
            return crud;
        }
        var a = start;
        var b = end;
        if (start == null) {
            a = LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0);
        }
        if (end == null) {
            b = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999_999_999);
        }
        if (a.toLocalDate().equals(b.toLocalDate()) && a.getHour() == b.getHour() && a.getMinute() == b.getMinute() && a.getSecond() == b.getSecond() && a.getNano() == b.getNano()) {
            var dayStart = a.toLocalDate().atStartOfDay();
            var dayEnd = a.toLocalDate().atTime(23, 59, 59, 999_999_999);
            return notBetween(field, new LocalDateTime[]{dayStart, dayEnd});
        } else {
            return notBetween(field, new LocalDateTime[]{a, b});
        }
    }

    @NotNull
    public SUD notBetween(@NotNull FieldGetter<T, LocalDateTime> field, @Nullable LocalDateTime start, @Nullable LocalDateTime end, boolean if0) {
        return if0 ? between(field, start, end) : crud;
    }

    // NOT BETWEEN - LocalDate
    @NotNull
    public SUD notBetween(@NotNull FieldGetter<T, LocalDate> field, @Nullable LocalDate start, @Nullable LocalDate end) {
        if (start == null && end == null) {
            return crud;
        }
        var a = start;
        var b = end;
        if (start == null) {
            a = LocalDate.of(1970, 1, 1);
        }
        if (end == null) {
            b = LocalDate.now();
        }
        return notBetween(field, new LocalDate[]{a, b});
    }

    @NotNull
    public SUD notBetween(@NotNull FieldGetter<T, LocalDate> field, @Nullable LocalDate start, @Nullable LocalDate end, boolean if0) {
        return if0 ? between(field, start, end) : crud;
    }

    // NOT BETWEEN - LocalTime
    @NotNull
    public SUD notBetween(@NotNull FieldGetter<T, LocalTime> field, @Nullable LocalTime start, @Nullable LocalTime end) {
        if (start == null && end == null) {
            return crud;
        }
        var a = start;
        var b = end;
        if (start == null) {
            a = LocalTime.MIN;
        }
        if (end == null) {
            b = LocalTime.MAX;
        }
        if (a.equals(b)) {
            return notBetween(field, new LocalTime[]{LocalTime.MIN, LocalTime.MAX});
        } else {
            return notBetween(field, new LocalTime[]{a, b});
        }
    }

    @NotNull
    public SUD notBetween(@NotNull FieldGetter<T, LocalTime> field, @Nullable LocalTime start, @Nullable LocalTime end, boolean if0) {
        return if0 ? between(field, start, end) : crud;
    }
    //endregion
}