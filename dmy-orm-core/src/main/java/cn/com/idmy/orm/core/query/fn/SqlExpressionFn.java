package cn.com.idmy.orm.core.query.fn;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true, chain = false)
@RequiredArgsConstructor
public class SqlExpressionFn {
    private final String field;
    private String expr;

    public SqlExpressionFn plus(int value) {
        this.expr = field + " + " + value;
        return this;
    }

    public SqlExpressionFn minus(int value) {
        this.expr = field + " - " + value;
        return this;
    }

    public SqlExpressionFn multiply(int value) {
        this.expr = field + " * " + value;
        return this;
    }
}
