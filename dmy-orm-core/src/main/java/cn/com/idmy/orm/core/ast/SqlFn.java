package cn.com.idmy.orm.core.ast;

import cn.com.idmy.orm.core.ast.Node.Field;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true, chain = false)
@RequiredArgsConstructor
public class SqlFn<T> {
    private final SqlFnName name;
    private final Field field;
    private Object expr;

    public SqlFn(SqlFnName name, Field field, Object expr) {
        this.name = name;
        this.field = field;
        this.expr = expr;
    }

    public static <T> SqlFn<T> count() {
        return new SqlFn<>(SqlFnName.COUNT, new Field("*"), "");
    }

    public static <T> SqlFn<T> count(FieldGetter<T, ?> getter) {
        return new SqlFn<>(SqlFnName.COUNT, new Field(getter), "");
    }

    public static <T> SqlFn<T> sum(FieldGetter<T, ?> getter) {
        return new SqlFn<>(SqlFnName.SUM, new Field(getter));
    }


    public static <T> SqlFn<T> max(FieldGetter<T, ?> getter) {
        return new SqlFn<>(SqlFnName.MAX, new Field(getter));
    }


    public static <T> SqlFn<T> min(FieldGetter<T, ?> getter) {
        return new SqlFn<>(SqlFnName.MIN, new Field(getter));
    }


    public static <T> SqlFn<T> avg(FieldGetter<T, ?> getter) {
        return new SqlFn<>(SqlFnName.AVG, new Field(getter));
    }


    public static <T> SqlFn<T> abs(FieldGetter<T, ?> getter) {
        return new SqlFn<>(SqlFnName.ABS, new Field(getter));
    }


    public static <T> SqlFn<T> length(FieldGetter<T, ?> getter) {
        return new SqlFn<>(SqlFnName.LENGTH, new Field(getter));
    }


    public static <T> SqlFn<T> ifNull(FieldGetter<T, ?> getter, Object value) {
        return new SqlFn<>(SqlFnName.IF_NULL, new Field(getter), value);
    }
}
