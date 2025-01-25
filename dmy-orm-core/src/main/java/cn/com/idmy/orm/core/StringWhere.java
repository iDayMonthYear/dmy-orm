package cn.com.idmy.orm.core;

import cn.com.idmy.orm.core.SqlNode.SqlCond;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.collection.CollUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;

@Slf4j
@Accessors(fluent = true, chain = false)
public abstract class StringWhere<T, SUD extends Where<T, SUD>> extends Crud<T, SUD> {
    protected StringWhere(@NotNull Class<T> entityType) {
        super(entityType);
    }

    //region 比较操作
    // 等于



    //region 包含操作
    // IN
    @NotNull
    public SUD in(@NotNull FieldGetter<T, ?> field, @Nullable Object... vals) {
        return ArrayUtil.isEmpty(vals) ? crud : addNode(new SqlCond(entityType, field, Op.IN, vals));
    }

    @NotNull
    public SUD in(@NotNull FieldGetter<T, ?> field, @Nullable Collection<?> vals) {
        return CollUtil.isEmpty(vals) ? crud : addNode(new SqlCond(entityType, field, Op.IN, vals));
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
    public SUD notIn(@NotNull FieldGetter<T, ?> field, @Nullable Object... vals) {
        return ArrayUtil.isEmpty(vals) ? crud : addNode(new SqlCond(entityType, field, Op.NOT_IN, vals));
    }

    @NotNull
    public SUD notIn(@NotNull FieldGetter<T, ?> field, @Nullable Collection<?> vals) {
        return CollUtil.isEmpty(vals) ? crud : addNode(new SqlCond(entityType, field, Op.NOT_IN, vals));
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
            return addNode(new SqlCond(entityType, field, Op.IS_NULL, true));
        } else {
            return addNode(new SqlCond(entityType, field, Op.IS_NOT_NULL, true));
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
            return addNode(new SqlCond(entityType, field, Op.BETWEEN, pair));
        }
    }

    @NotNull
    public SUD between(@NotNull FieldGetter<T, ?> field, @Nullable Object start, @Nullable Object end) {
        if (start == null || end == null) {
            return crud;
        } else {
            return addNode(new SqlCond(entityType, field, Op.BETWEEN, new Object[]{start, end}));
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
            return addNode(new SqlCond(entityType, field, Op.NOT_BETWEEN, pair));
        }
    }

    @NotNull
    public SUD notBetween(@NotNull FieldGetter<T, ?> field, @Nullable Object start, @Nullable Object end) {
        if (start == null || end == null) {
            return crud;
        } else {
            return addNode(new SqlCond(entityType, field, Op.NOT_BETWEEN, new Object[]{start, end}));
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