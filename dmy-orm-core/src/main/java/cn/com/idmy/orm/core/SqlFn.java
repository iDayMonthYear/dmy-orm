package cn.com.idmy.orm.core;

import cn.com.idmy.base.FieldGetter;
import cn.com.idmy.base.util.LambdaUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true, chain = false)
public class SqlFn<T> {
    private @NotNull SqlFnName name;
    private @NotNull String column;
    private @Nullable Object value;

    public SqlFn(@NonNull SqlFnName name, @NonNull FieldGetter<T, ?> field, @Nullable Object value) {
        this.column = Tables.getColumnName(LambdaUtil.getImplClass(field), field);
        this.name = name;
        this.value = value;
    }

    public SqlFn(@NonNull SqlFnName name, @NonNull FieldGetter<T, ?> field) {
        this(name, field, null);
    }

    public SqlFn(@NonNull SqlFnName name) {
        this.name = name;
        column = "*";
        value = null;
    }

    public static @NotNull <T> SqlFn<T> count() {
        return new SqlFn<>(SqlFnName.COUNT);
    }

    public static @NotNull <T> SqlFn<T> count(@NotNull FieldGetter<T, ?> field) {
        return new SqlFn<>(SqlFnName.COUNT, field);
    }

    public static @NotNull <T> SqlFn<T> sum(@NotNull FieldGetter<T, ?> field) {
        return new SqlFn<>(SqlFnName.SUM, field);
    }

    public static @NotNull <T> SqlFn<T> max(@NotNull FieldGetter<T, ?> field) {
        return new SqlFn<>(SqlFnName.MAX, field);
    }

    public static @NotNull <T> SqlFn<T> min(@NotNull FieldGetter<T, ?> field) {
        return new SqlFn<>(SqlFnName.MIN, field);
    }

    public static @NotNull <T> SqlFn<T> avg(@NotNull FieldGetter<T, ?> field) {
        return new SqlFn<>(SqlFnName.AVG, field);
    }

    public static @NotNull <T> SqlFn<T> abs(@NotNull FieldGetter<T, ?> field) {
        return new SqlFn<>(SqlFnName.ABS, field);
    }

    public static @NotNull <T> SqlFn<T> length(@NotNull FieldGetter<T, ?> field) {
        return new SqlFn<>(SqlFnName.LENGTH, field);
    }

    public static @NotNull <T> SqlFn<T> ifNull(@NotNull FieldGetter<T, ?> field, @NotNull Object value) {
        return new SqlFn<>(SqlFnName.IF_NULL, field, value);
    }
}