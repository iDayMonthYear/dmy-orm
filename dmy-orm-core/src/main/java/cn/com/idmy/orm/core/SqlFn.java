package cn.com.idmy.orm.core;

import cn.com.idmy.base.util.LambdaUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Accessors(fluent = true, chain = false)
public class SqlFn<T> {
    @NotNull
    private final SqlFnName name;
    @NotNull
    private final String column;
    @Nullable
    private final Object value;

    public SqlFn(@NonNull SqlFnName name, @NonNull FieldGetter<T, ?> field, @Nullable Object value) {
        this.name = name;
        this.column = Tables.getColumnName(LambdaUtil.getImplClass(field), field);
        this.value = value;
    }

    public SqlFn(@NonNull SqlFnName name, @NonNull FieldGetter<T, ?> field) {
        this(name, field, null);
    }

    @NotNull
    public static <T> SqlFn<T> count() {
        return new SqlFn<>(SqlFnName.COUNT, null);
    }

    @NotNull
    public static <T> SqlFn<T> count(@NotNull FieldGetter<T, ?> field) {
        return new SqlFn<>(SqlFnName.COUNT, field);
    }

    @NotNull
    public static <T> SqlFn<T> sum(@NotNull FieldGetter<T, ?> field) {
        return new SqlFn<>(SqlFnName.SUM, field);
    }

    @NotNull
    public static <T> SqlFn<T> max(@NotNull FieldGetter<T, ?> field) {
        return new SqlFn<>(SqlFnName.MAX, field);
    }

    @NotNull
    public static <T> SqlFn<T> min(@NotNull FieldGetter<T, ?> field) {
        return new SqlFn<>(SqlFnName.MIN, field);
    }

    @NotNull
    public static <T> SqlFn<T> avg(@NotNull FieldGetter<T, ?> field) {
        return new SqlFn<>(SqlFnName.AVG, field);
    }

    @NotNull
    public static <T> SqlFn<T> abs(@NotNull FieldGetter<T, ?> field) {
        return new SqlFn<>(SqlFnName.ABS, field);
    }

    @NotNull
    public static <T> SqlFn<T> length(@NotNull FieldGetter<T, ?> field) {
        return new SqlFn<>(SqlFnName.LENGTH, field);
    }

    @NotNull
    public static <T> SqlFn<T> ifNull(@NotNull FieldGetter<T, ?> field, @NotNull Object value) {
        return new SqlFn<>(SqlFnName.IF_NULL, field, value);
    }
}
