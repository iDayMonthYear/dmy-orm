package cn.com.idmy.orm.core;

import cn.com.idmy.base.FieldGetter;
import cn.com.idmy.base.exception.BizException;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.SqlNode.SqlCond;
import cn.com.idmy.orm.core.SqlNode.SqlOr;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.array.ArrayUtil;
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
public abstract class Where<T, ID, CRUD extends Where<T, ID, CRUD>> extends Crud<T, ID, CRUD> {
    protected Where(@NotNull Class<T> entityType) {
        super(entityType);
    }

    @NotNull
    public CRUD addNode(@NotNull SqlNode.SqlCond node) {
        if (ObjUtil.isEmpty(node.expr)) {
            if (nullable) {
                return crud;
            } else {
                throw new OrmException("表达式【{}】参数【{}】不能为空", node.type, node.column);
            }
        }
        return super.addNode(node);
    }

    //region 比较操作
    // 等于
    @NotNull
    public CRUD eq(@NonNull ID id) {
        if (ObjUtil.isEmpty(id)) {
            throw new BizException("主键不能为空");
        } else {
            return addNode(new SqlCond(Tables.getIdName(entityType), Op.EQ, id));
        }
    }

    @NotNull
    public CRUD eq(@NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return addNode(new SqlCond(entityType, field, Op.EQ, val));
    }

    @NotNull
    public CRUD eq(@NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr) {
        return addNode(new SqlCond(entityType, field, Op.EQ, expr));
    }

    @NotNull
    public CRUD eq(boolean if0, @NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return if0 ? eq(field, val) : crud;
    }

    @NotNull
    public CRUD eq(boolean if0, @NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr) {
        return if0 ? eq(field, expr) : crud;
    }

    @NotNull
    public CRUD eq(@NotNull FieldGetter<T, ?> field, @Nullable String val) {
        return addNode(new SqlCond(entityType, field, Op.EQ, val));
    }

    // 不等于
    @NotNull
    public CRUD ne(@NonNull ID id) {
        if (ObjUtil.isEmpty(id)) {
            throw new BizException("主键不能为空");
        } else {
            return addNode(new SqlCond(Tables.getIdName(entityType), Op.EQ, id));
        }
    }

    @NotNull
    public CRUD ne(@NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return addNode(new SqlCond(entityType, field, Op.NE, val));
    }

    @NotNull
    public CRUD ne(@NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr) {
        return addNode(new SqlCond(entityType, field, Op.NE, expr));
    }

    @NotNull
    public CRUD ne(boolean if0, @NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return if0 ? ne(field, val) : crud;
    }

    @NotNull
    public CRUD ne(boolean if0, @NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr) {
        return if0 ? ne(field, expr) : crud;
    }

    @NotNull
    public CRUD ne(@NotNull FieldGetter<T, ?> field, @Nullable String val) {
        return addNode(new SqlCond(entityType, field, Op.NE, val));
    }

    // 大于
    @NotNull
    public CRUD gt(@NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return addNode(new SqlCond(entityType, field, Op.GT, val));
    }

    @NotNull
    public CRUD gt(@NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr) {
        return addNode(new SqlCond(entityType, field, Op.GT, expr));
    }

    @NotNull
    public CRUD gt(@NotNull FieldGetter<T, ?> field, @Nullable LocalDateTime val) {
        if (val != null) {
            val = val.withHour(0).withMinute(0).withSecond(0).withNano(0);
        }
        return addNode(new SqlCond(entityType, field, Op.GT, val));
    }

    @NotNull
    public CRUD gt(boolean if0, @NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return if0 ? gt(field, val) : crud;
    }

    @NotNull
    public CRUD gt(boolean if0, @NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr) {
        return if0 ? gt(field, expr) : crud;
    }

    @NotNull
    public CRUD gt(boolean if0, @NotNull FieldGetter<T, ?> field, @Nullable LocalDateTime val) {
        return if0 ? gt(field, val) : crud;
    }

    // 大于等于
    @NotNull
    public CRUD ge(@NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return addNode(new SqlCond(entityType, field, Op.GE, val));
    }

    @NotNull
    public CRUD ge(@NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr) {
        return addNode(new SqlCond(entityType, field, Op.GE, expr));
    }

    @NotNull
    public CRUD ge(boolean if0, @NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return if0 ? ge(field, val) : crud;
    }

    @NotNull
    public CRUD ge(boolean if0, @NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr) {
        return if0 ? ge(field, expr) : crud;
    }

    // 小于
    @NotNull
    public CRUD lt(@NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return addNode(new SqlCond(entityType, field, Op.LT, val));
    }

    @NotNull
    public CRUD lt(@NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr) {
        return addNode(new SqlCond(entityType, field, Op.LT, expr));
    }

    @NotNull
    public CRUD lt(@NotNull FieldGetter<T, ?> field, @Nullable LocalDateTime val) {
        if (val != null) {
            val = val.withHour(23).withMinute(59).withSecond(59).withNano(999_999_999);
        }
        return addNode(new SqlCond(entityType, field, Op.LT, val));
    }

    @NotNull
    public CRUD lt(boolean if0, @NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return if0 ? lt(field, val) : crud;
    }

    @NotNull
    public CRUD lt(boolean if0, @NotNull FieldGetter<T, ?> field, SqlOpExpr expr) {
        return if0 ? lt(field, expr) : crud;
    }

    @NotNull
    public CRUD lt(boolean if0, @NotNull FieldGetter<T, ?> field, LocalDateTime val) {
        return if0 ? lt(field, val) : crud;
    }

    // 小于等于
    @NotNull
    public CRUD le(@NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return addNode(new SqlCond(entityType, field, Op.LE, val));
    }

    @NotNull
    public CRUD le(@NotNull FieldGetter<T, ?> field, SqlOpExpr expr) {
        return addNode(new SqlCond(entityType, field, Op.LE, expr));
    }

    @NotNull
    public CRUD le(boolean if0, @NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return if0 ? le(field, val) : crud;
    }

    @NotNull
    public CRUD le(boolean if0, @NotNull FieldGetter<T, ?> field, SqlOpExpr expr) {
        return if0 ? le(field, expr) : crud;
    }
    //endregion

    //region 字符串操作

    @NotNull
    public CRUD like(@NotNull FieldGetter<T, String> field, @Nullable String val) {
        return StrUtil.isBlank(val) ? crud : addNode(new SqlCond(entityType, field, Op.LIKE, "%" + val + "%"));
    }

    @NotNull
    public CRUD like(boolean if0, @NotNull FieldGetter<T, String> field, @Nullable String val) {
        return if0 ? like(field, val) : crud;
    }

    @NotNull
    public CRUD startsWith(@NotNull FieldGetter<T, String> field, @Nullable String val) {
        return StrUtil.isBlank(val) ? crud : addNode(new SqlCond(entityType, field, Op.LIKE, val + "%"));
    }

    @NotNull
    public CRUD startsWith(boolean if0, @NotNull FieldGetter<T, String> field, @Nullable String val) {
        return if0 ? startsWith(field, val) : crud;
    }

    @NotNull
    public CRUD endsWith(@NotNull FieldGetter<T, String> field, @Nullable String val) {
        return StrUtil.isBlank(val) ? crud : addNode(new SqlCond(entityType, field, Op.LIKE, "%" + val));
    }

    @NotNull
    public CRUD endsWith(boolean if0, @NotNull FieldGetter<T, String> field, @Nullable String val) {
        return if0 ? endsWith(field, val) : crud;
    }

    @NotNull
    public CRUD notLike(@NotNull FieldGetter<T, String> field, @Nullable String val) {
        return StrUtil.isBlank(val) ? crud : addNode(new SqlCond(entityType, field, Op.NOT_LIKE, "%" + val + "%"));
    }

    @NotNull
    public CRUD notLike(boolean if0, @NotNull FieldGetter<T, String> field, @Nullable String val) {
        return if0 ? notLike(field, val) : crud;
    }

    @NotNull
    public CRUD notStartsWith(@NotNull FieldGetter<T, String> field, @Nullable String val) {
        return StrUtil.isBlank(val) ? crud : addNode(new SqlCond(entityType, field, Op.NOT_LIKE, val + "%"));
    }

    @NotNull
    public CRUD notStartsWith(boolean if0, @NotNull FieldGetter<T, String> field, @Nullable String val) {
        return if0 ? notStartsWith(field, val) : crud;
    }

    @NotNull
    public CRUD notEndsWith(@NotNull FieldGetter<T, String> field, @Nullable String val) {
        return StrUtil.isBlank(val) ? crud : addNode(new SqlCond(entityType, field, Op.NOT_LIKE, "%" + val));
    }

    @NotNull
    public CRUD notEndsWith(boolean if0, @NotNull FieldGetter<T, String> field, @Nullable String val) {
        return if0 ? notEndsWith(field, val) : crud;
    }
    //endregion

    //region 包含操作
    // IN
    @NotNull
    public CRUD in(@NotNull FieldGetter<T, ?> field, @NotNull Object val, @Nullable Object... vals) {
        var arr = ArrayUtil.isEmpty(vals) ? new Object[]{val} : ArrayUtil.addAll(new Object[]{val}, vals);
        return addNode(new SqlCond(entityType, field, Op.IN, arr));
    }

    @NotNull
    public CRUD in(@NotNull FieldGetter<T, ?> field, @Nullable Object[] vals) {
        return addNode(new SqlCond(entityType, field, Op.IN, vals));
    }

    @NotNull
    public CRUD in(@NotNull FieldGetter<T, ?> field, @Nullable Collection<?> vals) {
        return addNode(new SqlCond(entityType, field, Op.IN, vals));
    }

    @NotNull
    public CRUD in(boolean if0, @NotNull FieldGetter<T, ?> field, @Nullable Collection<?> vals) {
        return if0 ? in(field, vals) : crud;
    }

    @NotNull
    public CRUD in(boolean if0, @NotNull FieldGetter<T, ?> field, @Nullable Object[] vals) {
        return if0 ? in(field, vals) : crud;
    }

    // NOT IN

    @NotNull
    public CRUD notIn(@NotNull FieldGetter<T, ?> field, @NotNull Object val, @Nullable Object... vals) {
        var arr = ArrayUtil.isEmpty(vals) ? new Object[]{val} : ArrayUtil.addAll(new Object[]{val}, vals);
        return addNode(new SqlCond(entityType, field, Op.NOT_IN, arr));
    }

    @NotNull
    public CRUD notIn(@NotNull FieldGetter<T, ?> field, @Nullable Object[] vals) {
        return addNode(new SqlCond(entityType, field, Op.NOT_IN, vals));
    }

    @NotNull
    public CRUD notIn(@NotNull FieldGetter<T, ?> field, @Nullable Collection<?> vals) {
        return addNode(new SqlCond(entityType, field, Op.NOT_IN, vals));
    }

    @NotNull
    public CRUD notIn(boolean if0, @NotNull FieldGetter<T, ?> field, @Nullable Collection<?> vals) {
        return if0 ? in(field, vals) : crud;
    }

    @NotNull
    public CRUD notIn(boolean if0, @NotNull FieldGetter<T, ?> field, @Nullable Object[] vals) {
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
    public CRUD isNull(boolean if0, @NotNull FieldGetter<T, ?> field) {
        return nulls(field, if0 ? true : null);
    }

    @NotNull
    public CRUD isNotNull(@NotNull FieldGetter<T, ?> field) {
        return nulls(field, false);
    }

    @NotNull
    public CRUD isNotNull(boolean if0, @NotNull FieldGetter<T, ?> field) {
        return nulls(field, if0 ? false : null);
    }
    //endregion

    //region 范围操作
    // BETWEEN - 通用类型
    @NotNull
    public CRUD between(@NotNull FieldGetter<T, ?> field, @Nullable Object[] pair) {
        return addNode(new SqlCond(entityType, field, Op.BETWEEN, pair));
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
    public CRUD between(boolean if0, @NotNull FieldGetter<T, ?> field, @Nullable Object[] pair) {
        return if0 ? between(field, pair) : crud;
    }

    @NotNull
    public CRUD between(boolean if0, @NotNull FieldGetter<T, ?> field, @Nullable Object start, @Nullable Object end) {
        return if0 ? between(field, start, end) : crud;
    }

    // BETWEEN - LocalDateTime
    @NotNull
    public CRUD between(@NotNull FieldGetter<T, LocalDateTime> field, @Nullable LocalDateTime start, @Nullable LocalDateTime end) {
        if (start == null && end == null) {
            return between(field, null);
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
    public CRUD between(boolean if0, @NotNull FieldGetter<T, LocalDateTime> field, @Nullable LocalDateTime start, @Nullable LocalDateTime end) {
        return if0 ? between(field, start, end) : crud;
    }

    // BETWEEN - LocalDate
    @NotNull
    public CRUD between(@NotNull FieldGetter<T, LocalDate> field, @Nullable LocalDate start, @Nullable LocalDate end) {
        if (start == null && end == null) {
            return between(field, null);
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
    public CRUD between(boolean if0, @NotNull FieldGetter<T, LocalDate> field, @Nullable LocalDate start, @Nullable LocalDate end) {
        return if0 ? between(field, start, end) : crud;
    }

    // BETWEEN - LocalTime
    @NotNull
    public CRUD between(@NotNull FieldGetter<T, LocalTime> field, @Nullable LocalTime start, @Nullable LocalTime end) {
        if (start == null && end == null) {
            return between(field, null);
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
    public CRUD between(boolean if0, @NotNull FieldGetter<T, LocalTime> field, @Nullable LocalTime start, @Nullable LocalTime end) {
        return if0 ? between(field, start, end) : crud;
    }

    // NOT BETWEEN - 通用类型
    @NotNull
    public CRUD notBetween(@NotNull FieldGetter<T, ?> field, @Nullable Object[] pair) {
        return addNode(new SqlCond(entityType, field, Op.NOT_BETWEEN, pair));
    }

    @NotNull
    public CRUD notBetween(@NotNull FieldGetter<T, ?> field, @Nullable Object start, @Nullable Object end) {
        return addNode(new SqlCond(entityType, field, Op.NOT_BETWEEN, new Object[]{start, end}));
    }

    @NotNull
    public CRUD notBetween(boolean if0, @NotNull FieldGetter<T, ?> field, @Nullable Object[] pair) {
        return if0 ? between(field, pair) : crud;
    }

    @NotNull
    public CRUD notBetween(boolean if0, @NotNull FieldGetter<T, ?> field, @Nullable Object start, @Nullable Object end) {
        return if0 ? between(field, start, end) : crud;
    }

    // NOT BETWEEN - LocalDateTime
    @NotNull
    public CRUD notBetween(@NotNull FieldGetter<T, LocalDateTime> field, @Nullable LocalDateTime start, @Nullable LocalDateTime end) {
        if (start == null && end == null) {
            return notBetween(field, null);
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
    public CRUD notBetween(boolean if0, @NotNull FieldGetter<T, LocalDateTime> field, @Nullable LocalDateTime start, @Nullable LocalDateTime end) {
        return if0 ? between(field, start, end) : crud;
    }

    // NOT BETWEEN - LocalDate
    @NotNull
    public CRUD notBetween(@NotNull FieldGetter<T, LocalDate> field, @Nullable LocalDate start, @Nullable LocalDate end) {
        if (start == null && end == null) {
            return notBetween(field, null);
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
    public CRUD notBetween(boolean if0, @NotNull FieldGetter<T, LocalDate> field, @Nullable LocalDate start, @Nullable LocalDate end) {
        return if0 ? between(field, start, end) : crud;
    }

    // NOT BETWEEN - LocalTime
    @NotNull
    public CRUD notBetween(@NotNull FieldGetter<T, LocalTime> field, @Nullable LocalTime start, @Nullable LocalTime end) {
        if (start == null && end == null) {
            return notBetween(field, null);
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
    public CRUD notBetween(boolean if0, @NotNull FieldGetter<T, LocalTime> field, @Nullable LocalTime start, @Nullable LocalTime end) {
        return if0 ? between(field, start, end) : crud;
    }
    //endregion

    @NotNull
    public CRUD or(boolean if0, @NotNull Consumer<WhereOr<T, ID>> consumer) {
        return if0 ? or(consumer) : crud;
    }

    @NotNull
    public CRUD or(@NotNull Consumer<WhereOr<T, ID>> consumer) {
        var where = new WhereOr<T, ID>(entityType);
        consumer.accept(where);
        addNode(new SqlOr());
        nodes.addAll(where.nodes);
        return crud;
    }
}