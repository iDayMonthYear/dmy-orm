package cn.com.idmy.orm.core;

import cn.com.idmy.base.util.LambdaUtil;
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

    public SqlFn(SqlFnName name, FieldGetter<T, ?> field, Object value) {
        this.name = name;
        this.column = Tables.getColumnName(LambdaUtil.getImplClass(field), field);
        this.value = value;
    }

    public SqlFn(SqlFnName name, FieldGetter<T, ?> field) {
        this(name, field, null);
    }

    public static <T> SqlFn<T> count() {
        return new SqlFn<>(SqlFnName.COUNT, null);
    }

    public static <T> SqlFn<T> count(FieldGetter<T, ?> field) {
        return new SqlFn<>(SqlFnName.COUNT, field);
    }

    public static <T> SqlFn<T> sum(FieldGetter<T, ?> field) {
        return new SqlFn<>(SqlFnName.SUM, field);
    }

    public static <T> SqlFn<T> max(FieldGetter<T, ?> field) {
        return new SqlFn<>(SqlFnName.MAX, field);
    }

    public static <T> SqlFn<T> min(FieldGetter<T, ?> field) {
        return new SqlFn<>(SqlFnName.MIN, field);
    }

    public static <T> SqlFn<T> avg(FieldGetter<T, ?> field) {
        return new SqlFn<>(SqlFnName.AVG, field);
    }

    public static <T> SqlFn<T> abs(FieldGetter<T, ?> field) {
        return new SqlFn<>(SqlFnName.ABS, field);
    }

    public static <T> SqlFn<T> length(FieldGetter<T, ?> field) {
        return new SqlFn<>(SqlFnName.LENGTH, field);
    }

    public static <T> SqlFn<T> ifNull(FieldGetter<T, ?> field, Object value) {
        return new SqlFn<>(SqlFnName.IF_NULL, field, value);
    }
}
