package cn.com.idmy.orm.core;

import cn.com.idmy.base.FieldGetter;
import cn.com.idmy.base.util.Assert;
import cn.com.idmy.orm.core.SqlNode.SqlSet;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.lang.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Accessors(fluent = true, chain = false)
public class Update<T> extends Where<T, Update<T>> {
    protected Update(@NotNull Class<T> entityType, boolean nullable) {
        super(entityType);
        this.nullable = nullable;
    }

    public @NotNull Update<T> set(@NotNull FieldGetter<T, ?> field, @NotNull Object val) {
        Assert.notNull(val, "set 语句值不能为空，请使用 setNullable 方法设置 null 值");
        return addNode(new SqlSet(entityType, field, val));
    }

    public @NotNull Update<T> set(@NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr) {
        Assert.notNull(expr, "set 语句值不能为空，请使用 setNullable 方法设置 null 值");
        return addNode(new SqlSet(entityType, field, expr));
    }

    public @NotNull Update<T> set(boolean if0, @NotNull FieldGetter<T, ?> field, @NotNull Object val) {
        return if0 ? set(field, val) : crud;
    }

    public @NotNull Update<T> set(boolean if0, @NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr) {
        return if0 ? set(field, expr) : crud;
    }

    public @NotNull Update<T> setPlus(@NotNull FieldGetter<T, Number> field, @NotNull Number val) {
        return set(field, r -> r.plus(val));
    }

    public @NotNull Update<T> setPlus(boolean if0, @NotNull FieldGetter<T, Number> field, @NotNull Number val) {
        return if0 ? setPlus(field, val) : crud;
    }

    public @NotNull Update<T> setMinus(@NotNull FieldGetter<T, Number> field, @NotNull Number val) {
        return set(field, r -> r.minus(val));
    }

    public @NotNull Update<T> setMinus(boolean if0, @NotNull FieldGetter<T, Number> field, @NotNull Number val) {
        return if0 ? setMinus(field, val) : crud;
    }

    public @NotNull Update<T> setTrue(@NotNull FieldGetter<T, Boolean> field) {
        return addNode(new SqlSet(entityType, field, true));
    }

    public @NotNull Update<T> setTrue(boolean if0, @NotNull FieldGetter<T, Boolean> field) {
        return if0 ? setTrue(field) : crud;
    }

    public @NotNull Update<T> setFalse(@NotNull FieldGetter<T, Boolean> field) {
        return addNode(new SqlSet(entityType, field, false));
    }

    public @NotNull Update<T> setFalse(boolean if0, @NotNull FieldGetter<T, Boolean> field) {
        return if0 ? setFalse(field) : crud;
    }

    public @NotNull Update<T> setNull(@NotNull FieldGetter<T, ?> field) {
        return addNode(new SqlSet(entityType, field, null));
    }

    public @NotNull Update<T> setNull(boolean if0, @NotNull FieldGetter<T, ?> field) {
        return if0 ? setNull(field) : crud;
    }

    public @NotNull Update<T> setZero(@NotNull FieldGetter<T, Number> field) {
        return addNode(new SqlSet(entityType, field, 0));
    }

    public @NotNull Update<T> setZero(boolean if0, @NotNull FieldGetter<T, Number> field) {
        return if0 ? setZero(field) : crud;
    }

    public @NotNull Update<T> setNow(@NotNull FieldGetter<T, LocalDateTime> field) {
        return addNode(new SqlSet(entityType, field, LocalDateTime.now()));
    }

    public @NotNull Update<T> setNow(boolean if0, @NotNull FieldGetter<T, LocalDateTime> field) {
        return if0 ? setNow(field) : crud;
    }

    public @NotNull Update<T> setNullable(@NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return addNode(new SqlSet(entityType, field, val));
    }

    public @NotNull Update<T> setNullable(boolean if0, @NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return if0 ? setNullable(field, val) : crud;
    }

    @Override
    public @NotNull Pair<String, List<Object>> sql() {
        return new UpdateSqlGenerator(this).generate();
    }
}
