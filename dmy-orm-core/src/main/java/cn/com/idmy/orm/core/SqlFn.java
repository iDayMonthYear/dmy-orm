package cn.com.idmy.orm.core;

import cn.com.idmy.orm.util.LambdaUtil;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true, chain = false)
public class SqlFn<T> {
    private final SqlFnName name;
    private final String column;
    @Nullable
    private final Object value;

    public SqlFn(SqlFnName name, ColumnGetter<T, ?> column, Object value) {
        this.name = name;
        this.column = LambdaUtil.getFieldName(column);
        this.value = value;
    }

    public SqlFn(SqlFnName name, ColumnGetter<T, ?> column) {
        this(name, column, null);
    }

    public static <T> SqlFn<T> count() {
        return new SqlFn<>(SqlFnName.COUNT, null);
    }

    public static <T> SqlFn<T> count(ColumnGetter<T, ?> column) {
        return new SqlFn<>(SqlFnName.COUNT, column);
    }

    public static <T> SqlFn<T> sum(ColumnGetter<T, ?> column) {
        return new SqlFn<>(SqlFnName.SUM, column);
    }

    public static <T> SqlFn<T> max(ColumnGetter<T, ?> column) {
        return new SqlFn<>(SqlFnName.MAX, column);
    }

    public static <T> SqlFn<T> min(ColumnGetter<T, ?> column) {
        return new SqlFn<>(SqlFnName.MIN, column);
    }

    public static <T> SqlFn<T> avg(ColumnGetter<T, ?> column) {
        return new SqlFn<>(SqlFnName.AVG, column);
    }

    public static <T> SqlFn<T> abs(ColumnGetter<T, ?> column) {
        return new SqlFn<>(SqlFnName.ABS, column);
    }

    public static <T> SqlFn<T> length(ColumnGetter<T, ?> column) {
        return new SqlFn<>(SqlFnName.LENGTH, column);
    }

    public static <T> SqlFn<T> ifNull(ColumnGetter<T, ?> column, Object value) {
        return new SqlFn<>(SqlFnName.IF_NULL, column, value);
    }
}
