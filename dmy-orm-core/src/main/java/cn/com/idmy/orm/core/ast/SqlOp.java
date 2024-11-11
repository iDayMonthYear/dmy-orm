package cn.com.idmy.orm.core.ast;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true, chain = false)
@RequiredArgsConstructor
public class SqlOp {
    private final String field;
    private String expr;

    public SqlOp plus(int value) {
        this.expr = field + " + " + value;
        return this;
    }

    public SqlOp minus(int value) {
        this.expr = field + " - " + value;
        return this;
    }

    public SqlOp multiply(int value) {
        this.expr = field + " * " + value;
        return this;
    }

    public SqlOp divide(int value) {
        this.expr = field + " / " + value;
        return this;
    }

    public SqlOp mod(int value) {
        this.expr = field + " % " + value;
        return this;
    }
}
