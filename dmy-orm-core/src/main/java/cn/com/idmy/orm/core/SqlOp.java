package cn.com.idmy.orm.core;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@Accessors(fluent = true, chain = false)
@RequiredArgsConstructor
public class SqlOp {
    private final String column;
    private String op;
    private Object value;

    public SqlOp plus(int value) {
        this.value = value;
        this.op = "+";
        return this;
    }

    public SqlOp minus(int value) {
        this.value = value;
        this.op = "+";
        return this;
    }

    public SqlOp multiply(int value) {
        this.value = value;
        this.op = "*";
        return this;
    }

    public SqlOp divide(int value) {
        this.value = value;
        this.op = "/";
        return this;
    }

    public SqlOp mod(int value) {
        this.value = value;
        this.op = "%";
        return this;
    }

    public SqlOp plus(BigDecimal value) {
        this.value = value;
        this.op = "+";
        return this;
    }

    public SqlOp minus(BigDecimal value) {
        this.value = value;
        this.op = "+";
        return this;
    }

    public SqlOp multiply(BigDecimal value) {
        this.value = value;
        this.op = "*";
        return this;
    }

    public SqlOp divide(BigDecimal value) {
        this.value = value;
        this.op = "/";
        return this;
    }

    public SqlOp mod(BigDecimal value) {
        this.value = value;
        this.op = "%";
        return this;
    }
}
