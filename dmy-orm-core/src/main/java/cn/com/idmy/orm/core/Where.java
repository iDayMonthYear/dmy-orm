package cn.com.idmy.orm.core;

import cn.com.idmy.orm.core.SqlNode.SqlCond;
import cn.com.idmy.orm.core.SqlNode.SqlOr;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.core.util.ObjUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.function.Consumer;

@Slf4j
@Accessors(fluent = true, chain = false)
public abstract class Where<T, CRUD extends Where<T, CRUD>> extends Crud<T, CRUD> {
    protected Where(@NotNull Class<T> entityType) {
        super(entityType);
    }

    @NotNull
    public CRUD addNode(@NotNull SqlNode.SqlCond node) {
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
    public CRUD eq(@NonNull Object id) {
        return ObjUtil.isEmpty(id) ? crud : addNode(new SqlCond(Tables.getIdName(entityType), Op.EQ, id));
    }

    @NotNull
    public CRUD eq(@NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return ObjUtil.isEmpty(val) ? crud : addNode(new SqlCond(entityType, field, Op.EQ, val));
    }

    @NotNull
    public CRUD eq(@NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr) {
        return addNode(new SqlCond(entityType, field, Op.EQ, expr));
    }

    @NotNull
    public CRUD eq(@NotNull FieldGetter<T, ?> field, @Nullable Object val, boolean if0) {
        return if0 ? eq(field, val) : crud;
    }

    @NotNull
    public CRUD eq(@NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr, boolean if0) {
        return if0 ? eq(field, expr) : crud;
    }

    @NotNull
    public CRUD eq(@NotNull FieldGetter<T, ?> field, @Nullable String val) {
        return ObjUtil.isEmpty(val) ? crud : addNode(new SqlCond(entityType, field, Op.EQ, val));
    }

    // 不等于
    @NotNull
    public CRUD ne(@NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return ObjUtil.isEmpty(val) ? crud : addNode(new SqlCond(entityType, field, Op.NE, val));
    }

    @NotNull
    public CRUD ne(@NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr) {
        return addNode(new SqlCond(entityType, field, Op.NE, expr));
    }

    @NotNull
    public CRUD ne(@NotNull FieldGetter<T, ?> field, @Nullable Object val, boolean if0) {
        return if0 ? ne(field, val) : crud;
    }

    @NotNull
    public CRUD ne(@NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr, boolean if0) {
        return if0 ? ne(field, expr) : crud;
    }

    @NotNull
    public CRUD ne(@NotNull FieldGetter<T, ?> field, @Nullable String val) {
        return ObjUtil.isEmpty(val) ? crud : addNode(new SqlCond(entityType, field, Op.NE, val));
    }

    // 大于
    @NotNull
    public CRUD gt(@NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return ObjUtil.isEmpty(val) ? crud : addNode(new SqlCond(entityType, field, Op.GT, val));
    }

    @NotNull
    public CRUD gt(@NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr) {
        return addNode(new SqlCond(entityType, field, Op.GT, expr));
    }

    @NotNull
    public CRUD gt(@NotNull FieldGetter<T, ?> field, @Nullable LocalDateTime val) {
        if (ObjUtil.isEmpty(val)) {
            return crud;
        } else {
            var at = val.withHour(0).withMinute(0).withSecond(0).withNano(0);
            return addNode(new SqlCond(entityType, field, Op.GT, at));
        }
    }

    @NotNull
    public CRUD gt(@NotNull FieldGetter<T, ?> field, @Nullable Object val, boolean if0) {
        return if0 ? gt(field, val) : crud;
    }

    @NotNull
    public CRUD gt(@NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr, boolean if0) {
        return if0 ? gt(field, expr) : crud;
    }

    @NotNull
    public CRUD gt(@NotNull FieldGetter<T, ?> field, @Nullable LocalDateTime val, boolean if0) {
        return if0 ? gt(field, val) : crud;
    }

    // 大于等于
    @NotNull
    public CRUD ge(@NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return ObjUtil.isEmpty(val) ? crud : addNode(new SqlCond(entityType, field, Op.GE, val));
    }

    @NotNull
    public CRUD ge(@NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr) {
        return addNode(new SqlCond(entityType, field, Op.GE, expr));
    }

    @NotNull
    public CRUD ge(@NotNull FieldGetter<T, ?> field, @Nullable Object val, boolean if0) {
        return if0 ? ge(field, val) : crud;
    }

    @NotNull
    public CRUD ge(@NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr, boolean if0) {
        return if0 ? ge(field, expr) : crud;
    }

    // 小于
    @NotNull
    public CRUD lt(@NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return ObjUtil.isEmpty(val) ? crud : addNode(new SqlCond(entityType, field, Op.LT, val));
    }

    @NotNull
    public CRUD lt(@NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr) {
        return addNode(new SqlCond(entityType, field, Op.LT, expr));
    }

    @NotNull
    public CRUD lt(@NotNull FieldGetter<T, ?> field, @Nullable LocalDateTime val) {
        if (ObjUtil.isEmpty(val)) {
            return crud;
        } else {
            var at = val.withHour(23).withMinute(59).withSecond(59).withNano(999_999_999);
            return addNode(new SqlCond(entityType, field, Op.LT, at));
        }
    }

    @NotNull
    public CRUD lt(@NotNull FieldGetter<T, ?> field, @Nullable Object val, boolean if0) {
        return if0 ? lt(field, val) : crud;
    }

    @NotNull
    public CRUD lt(@NotNull FieldGetter<T, ?> field, SqlOpExpr expr, boolean if0) {
        return if0 ? lt(field, expr) : crud;
    }

    @NotNull
    public CRUD lt(@NotNull FieldGetter<T, ?> field, LocalDateTime val, boolean if0) {
        return if0 ? lt(field, val) : crud;
    }

    // 小于等于
    @NotNull
    public CRUD le(@NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return ObjUtil.isEmpty(val) ? crud : addNode(new SqlCond(entityType, field, Op.LE, val));
    }

    @NotNull
    public CRUD le(@NotNull FieldGetter<T, ?> field, SqlOpExpr expr) {
        return addNode(new SqlCond(entityType, field, Op.LE, expr));
    }

    @NotNull
    public CRUD le(@NotNull FieldGetter<T, ?> field, @Nullable Object val, boolean if0) {
        return if0 ? le(field, val) : crud;
    }

    @NotNull
    public CRUD le(@NotNull FieldGetter<T, ?> field, SqlOpExpr expr, boolean if0) {
        return if0 ? le(field, expr) : crud;
    }
    //endregion

    //region 字符串操作

    @NotNull
    public CRUD like(@NotNull FieldGetter<T, String> field, @Nullable String val) {
        return StrUtil.isBlank(val) ? crud : addNode(new SqlCond(entityType, field, Op.LIKE, "%" + val + "%"));
    }

    @NotNull
    public CRUD like(@NotNull FieldGetter<T, String> field, @Nullable String val, boolean if0) {
        return if0 ? like(field, val) : crud;
    }

    @NotNull
    public CRUD startsWith(@NotNull FieldGetter<T, String> field, @Nullable String val) {
        return StrUtil.isBlank(val) ? crud : addNode(new SqlCond(entityType, field, Op.LIKE, val + "%"));
    }

    @NotNull
    public CRUD startsWith(@NotNull FieldGetter<T, String> field, @Nullable String val, boolean if0) {
        return if0 ? startsWith(field, val) : crud;
    }

    @NotNull
    public CRUD endsWith(@NotNull FieldGetter<T, String> field, @Nullable String val) {
        return StrUtil.isBlank(val) ? crud : addNode(new SqlCond(entityType, field, Op.LIKE, "%" + val));
    }

    @NotNull
    public CRUD endsWith(@NotNull FieldGetter<T, String> field, @Nullable String val, boolean if0) {
        return if0 ? endsWith(field, val) : crud;
    }

    @NotNull
    public CRUD notLike(@NotNull FieldGetter<T, String> field, @Nullable String val) {
        return StrUtil.isBlank(val) ? crud : addNode(new SqlCond(entityType, field, Op.NOT_LIKE, "%" + val + "%"));
    }

    @NotNull
    public CRUD notLike(@NotNull FieldGetter<T, String> field, @Nullable String val, boolean if0) {
        return if0 ? notLike(field, val) : crud;
    }

    @NotNull
    public CRUD notStartsWith(@NotNull FieldGetter<T, String> field, @Nullable String val) {
        return StrUtil.isBlank(val) ? crud : addNode(new SqlCond(entityType, field, Op.NOT_LIKE, val + "%"));
    }

    @NotNull
    public CRUD notStartsWith(@NotNull FieldGetter<T, String> field, @Nullable String val, boolean if0) {
        return if0 ? notStartsWith(field, val) : crud;
    }

    @NotNull
    public CRUD notEndsWith(@NotNull FieldGetter<T, String> field, @Nullable String val) {
        return StrUtil.isBlank(val) ? crud : addNode(new SqlCond(entityType, field, Op.NOT_LIKE, "%" + val));
    }

    @NotNull
    public CRUD notEndsWith(@NotNull FieldGetter<T, String> field, @Nullable String val, boolean if0) {
        return if0 ? notEndsWith(field, val) : crud;
    }
    //endregion

    //region 包含操作
    // IN
    @NotNull
    public CRUD in(@NotNull FieldGetter<T, ?> field, @NotNull Object val, @Nullable Object... vals) {
        var arr = ArrayUtil.isEmpty(vals) ? new Object[]{val} : ArrayUtil.addAll(new Object[]{val}, vals);
        return ArrayUtil.isEmpty(vals) ? crud : addNode(new SqlCond(entityType, field, Op.IN, arr));
    }

    @NotNull
    public CRUD in(@NotNull FieldGetter<T, ?> field, @Nullable Object[] vals) {
        return ArrayUtil.isEmpty(vals) ? crud : addNode(new SqlCond(entityType, field, Op.IN, vals));
    }

    @NotNull
    public CRUD in(@NotNull FieldGetter<T, ?> field, @Nullable Collection<?> vals) {
        return CollUtil.isEmpty(vals) ? crud : addNode(new SqlCond(entityType, field, Op.IN, vals));
    }

    @NotNull
    public CRUD in(@NotNull FieldGetter<T, ?> field, @Nullable Collection<?> vals, boolean if0) {
        return if0 ? in(field, vals) : crud;
    }

    @NotNull
    public CRUD in(@NotNull FieldGetter<T, ?> field, @Nullable Object[] vals, boolean if0) {
        return if0 ? in(field, vals) : crud;
    }

    // NOT IN

    @NotNull
    public CRUD notIn(@NotNull FieldGetter<T, ?> field, @NotNull Object val, @Nullable Object... vals) {
        var arr = ArrayUtil.isEmpty(vals) ? new Object[]{val} : ArrayUtil.addAll(new Object[]{val}, vals);
        return ArrayUtil.isEmpty(vals) ? crud : addNode(new SqlCond(entityType, field, Op.NOT_IN, arr));
    }

    @NotNull
    public CRUD notIn(@NotNull FieldGetter<T, ?> field, @Nullable Object[] vals) {
        return ArrayUtil.isEmpty(vals) ? crud : addNode(new SqlCond(entityType, field, Op.NOT_IN, vals));
    }

    @NotNull
    public CRUD notIn(@NotNull FieldGetter<T, ?> field, @Nullable Collection<?> vals) {
        return CollUtil.isEmpty(vals) ? crud : addNode(new SqlCond(entityType, field, Op.NOT_IN, vals));
    }

    @NotNull
    public CRUD notIn(@NotNull FieldGetter<T, ?> field, @Nullable Collection<?> vals, boolean if0) {
        return if0 ? in(field, vals) : crud;
    }

    @NotNull
    public CRUD notIn(@NotNull FieldGetter<T, ?> field, @Nullable Object[] vals, boolean if0) {
        return if0 ? in(field, vals) : crud;
    }
    //endregion

    //region NULL值操作
    @NotNull
    public CRUD nulls(@NotNull FieldGetter<T, ?> field, @Nullable Boolean bol) {
        if (bol == null) {
            return crud;
        } else if (bol) {
            return addNode(new SqlCond(entityType, field, Op.IS_NULL, true));
        } else {
            return addNode(new SqlCond(entityType, field, Op.IS_NOT_NULL, true));
        }
    }

    @NotNull
    public CRUD isNull(@NotNull FieldGetter<T, ?> field) {
        return nulls(field, true);
    }

    @NotNull
    public CRUD isNull(@NotNull FieldGetter<T, ?> field, boolean if0) {
        return nulls(field, if0 ? true : null);
    }

    @NotNull
    public CRUD isNotNull(@NotNull FieldGetter<T, ?> field) {
        return nulls(field, false);
    }

    @NotNull
    public CRUD isNotNull(@NotNull FieldGetter<T, ?> field, boolean if0) {
        return nulls(field, if0 ? false : null);
    }
    //endregion

    //region 范围操作
    // BETWEEN - 通用类型
    @NotNull
    public CRUD between(@NotNull FieldGetter<T, ?> field, @Nullable Object[] pair) {
        if (pair == null || pair.length != 2) {
            return crud;
        } else {
            return addNode(new SqlCond(entityType, field, Op.BETWEEN, pair));
        }
    }

    @NotNull
    public CRUD between(@NotNull FieldGetter<T, ?> field, @Nullable Object start, @Nullable Object end) {
        if (start == null || end == null) {
            return crud;
        } else {
            return addNode(new SqlCond(entityType, field, Op.BETWEEN, new Object[]{start, end}));
        }
    }

    @NotNull
    public CRUD between(@NotNull FieldGetter<T, ?> field, @Nullable Object[] pair, boolean if0) {
        return if0 ? between(field, pair) : crud;
    }

    @NotNull
    public CRUD between(@NotNull FieldGetter<T, ?> field, @Nullable Object start, @Nullable Object end, boolean if0) {
        return if0 ? between(field, start, end) : crud;
    }

    // BETWEEN - LocalDateTime
    @NotNull
    public CRUD between(@NotNull FieldGetter<T, LocalDateTime> field, @Nullable LocalDateTime start, @Nullable LocalDateTime end) {
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
    public CRUD between(@NotNull FieldGetter<T, LocalDateTime> field, @Nullable LocalDateTime start, @Nullable LocalDateTime end, boolean if0) {
        return if0 ? between(field, start, end) : crud;
    }

    // BETWEEN - LocalDate
    @NotNull
    public CRUD between(@NotNull FieldGetter<T, LocalDate> field, @Nullable LocalDate start, @Nullable LocalDate end) {
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
    public CRUD between(@NotNull FieldGetter<T, LocalDate> field, @Nullable LocalDate start, @Nullable LocalDate end, boolean if0) {
        return if0 ? between(field, start, end) : crud;
    }

    // BETWEEN - LocalTime
    @NotNull
    public CRUD between(@NotNull FieldGetter<T, LocalTime> field, @Nullable LocalTime start, @Nullable LocalTime end) {
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
    public CRUD between(@NotNull FieldGetter<T, LocalTime> field, @Nullable LocalTime start, @Nullable LocalTime end, boolean if0) {
        return if0 ? between(field, start, end) : crud;
    }

    // NOT BETWEEN - 通用类型
    @NotNull
    public CRUD notBetween(@NotNull FieldGetter<T, ?> field, @Nullable Object[] pair) {
        if (pair == null || pair.length != 2) {
            return crud;
        } else {
            return addNode(new SqlCond(entityType, field, Op.NOT_BETWEEN, pair));
        }
    }

    @NotNull
    public CRUD notBetween(@NotNull FieldGetter<T, ?> field, @Nullable Object start, @Nullable Object end) {
        if (start == null || end == null) {
            return crud;
        } else {
            return addNode(new SqlCond(entityType, field, Op.NOT_BETWEEN, new Object[]{start, end}));
        }
    }

    @NotNull
    public CRUD notBetween(@NotNull FieldGetter<T, ?> field, @Nullable Object[] pair, boolean if0) {
        return if0 ? between(field, pair) : crud;
    }

    @NotNull
    public CRUD notBetween(@NotNull FieldGetter<T, ?> field, @Nullable Object start, @Nullable Object end, boolean if0) {
        return if0 ? between(field, start, end) : crud;
    }

    // NOT BETWEEN - LocalDateTime
    @NotNull
    public CRUD notBetween(@NotNull FieldGetter<T, LocalDateTime> field, @Nullable LocalDateTime start, @Nullable LocalDateTime end) {
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
    public CRUD notBetween(@NotNull FieldGetter<T, LocalDateTime> field, @Nullable LocalDateTime start, @Nullable LocalDateTime end, boolean if0) {
        return if0 ? between(field, start, end) : crud;
    }

    // NOT BETWEEN - LocalDate
    @NotNull
    public CRUD notBetween(@NotNull FieldGetter<T, LocalDate> field, @Nullable LocalDate start, @Nullable LocalDate end) {
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
    public CRUD notBetween(@NotNull FieldGetter<T, LocalDate> field, @Nullable LocalDate start, @Nullable LocalDate end, boolean if0) {
        return if0 ? between(field, start, end) : crud;
    }

    // NOT BETWEEN - LocalTime
    @NotNull
    public CRUD notBetween(@NotNull FieldGetter<T, LocalTime> field, @Nullable LocalTime start, @Nullable LocalTime end) {
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
    public CRUD notBetween(@NotNull FieldGetter<T, LocalTime> field, @Nullable LocalTime start, @Nullable LocalTime end, boolean if0) {
        return if0 ? between(field, start, end) : crud;
    }
    //endregion

    @NotNull
    public CRUD or(@NotNull Consumer<WhereOr<T>> consumer) {
        var where = new WhereOr<>(entityType);
        consumer.accept(where);
        addNode(new SqlOr());
        nodes.addAll(where.nodes);
        return crud;
    }
}