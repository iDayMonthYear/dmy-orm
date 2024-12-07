package cn.com.idmy.orm.core;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
@Accessors(fluent = true, chain = false)
public class SqlOp {
    private final String column;
    private String op;
    private Object value;

    public SqlOp plus(long val) {
        value = val;
        op = "+";
        return this;
    }

    public SqlOp minus(long val) {
        value = val;
        op = "-";
        return this;
    }

    public SqlOp multiply(long val) {
        value = val;
        op = "*";
        return this;
    }

    public SqlOp divide(long val) {
        value = val;
        op = "/";
        return this;
    }

    public SqlOp mod(long val) {
        value = val;
        op = "%";
        return this;
    }

    public SqlOp plus(BigDecimal val) {
        value = val;
        op = "+";
        return this;
    }

    public SqlOp minus(BigDecimal val) {
        value = val;
        op = "-";
        return this;
    }

    public SqlOp multiply(BigDecimal val) {
        value = val;
        op = "*";
        return this;
    }

    public SqlOp divide(BigDecimal val) {
        value = val;
        op = "/";
        return this;
    }

    public SqlOp mod(BigDecimal val) {
        value = val;
        op = "%";
        return this;
    }
}
