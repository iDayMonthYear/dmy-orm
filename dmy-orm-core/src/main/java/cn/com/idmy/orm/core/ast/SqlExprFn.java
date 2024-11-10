package cn.com.idmy.orm.core.ast;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true, chain = false)
@RequiredArgsConstructor
public class SqlExprFn {
    private final String field;
    private String expr;

    public SqlExprFn plus(int value) {
        this.expr = field + " + " + value;
        return this;
    }

    public SqlExprFn minus(int value) {
        this.expr = field + " - " + value;
        return this;
    }

    public SqlExprFn multiply(int value) {
        this.expr = field + " * " + value;
        return this;
    }

    public SqlExprFn divide(int value) {
        this.expr = field + " / " + value;
        return this;
    }

    public SqlExprFn mod(int value) {
        this.expr = field + " % " + value;
        return this;
    }
}
