package cn.com.idmy.orm.core;

import cn.com.idmy.base.FieldGetter;
import cn.com.idmy.base.model.Pair;
import cn.com.idmy.base.util.Assert;
import cn.com.idmy.orm.core.SqlNode.SqlSet;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


@Slf4j
@Accessors(fluent = true, chain = false)
public class Update<T> extends Where<T, Update<T>> {
    protected Update(@NotNull Class<T> entityType, boolean nullable) {
        super(entityType);
        this.nullable = nullable;
    }

    @NotNull
    public Update<T> set(@NotNull FieldGetter<T, ?> field, @NotNull Object val) {
        Assert.notNull(val, "set 语句值不能为空，请使用 setNullable 方法设置 null 值");
        return addNode(new SqlSet(entityType, field, val));
    }

    @NotNull
    public Update<T> set(@NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr) {
        Assert.notNull(expr, "set 语句值不能为空，请使用 setNullable 方法设置 null 值");
        return addNode(new SqlSet(entityType, field, expr));
    }

    @NotNull
    public Update<T> set(boolean if0, @NotNull FieldGetter<T, ?> field, @NotNull Object val) {
        return if0 ? set(field, val) : crud;
    }

    @NotNull
    public Update<T> set(boolean if0, @NotNull FieldGetter<T, ?> field, @NotNull SqlOpExpr expr) {
        return if0 ? set(field, expr) : crud;
    }

    @NotNull
    public Update<T> setTrue(@NotNull FieldGetter<T, Boolean> field) {
        return addNode(new SqlSet(entityType, field, true));
    }

    @NotNull
    public Update<T> setTrue(boolean if0, @NotNull FieldGetter<T, Boolean> field) {
        return if0 ? setTrue(field) : crud;
    }

    @NotNull
    public Update<T> setFalse(@NotNull FieldGetter<T, Boolean> field) {
        return addNode(new SqlSet(entityType, field, false));
    }

    @NotNull
    public Update<T> setFalse(boolean if0, @NotNull FieldGetter<T, Boolean> field) {
        return if0 ? setFalse(field) : crud;
    }

    @NotNull
    public Update<T> setNull(@NotNull FieldGetter<T, ?> field) {
        return addNode(new SqlSet(entityType, field, null));
    }

    @NotNull
    public Update<T> setNull(boolean if0, @NotNull FieldGetter<T, ?> field) {
        return if0 ? setNull(field) : crud;
    }

    @NotNull
    public Update<T> setZero(@NotNull FieldGetter<T, Number> field) {
        return addNode(new SqlSet(entityType, field, 0));
    }

    @NotNull
    public Update<T> setZero(boolean if0, @NotNull FieldGetter<T, Number> field) {
        return if0 ? setZero(field) : crud;
    }

    @NotNull
    public Update<T> setNullable(@NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return addNode(new SqlSet(entityType, field, val));
    }

    @NotNull
    public Update<T> setNullable(boolean if0, @NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return if0 ? setNullable(field, val) : crud;
    }

    @NotNull
    @Override
    public Pair<String, List<Object>> sql() {
        return new UpdateSqlGenerator(this).generate();
    }
}
