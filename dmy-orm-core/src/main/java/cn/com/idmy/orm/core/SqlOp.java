package cn.com.idmy.orm.core;


import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
@Accessors(fluent = true, chain = false)
public class SqlOp {
    private final String column;
    @NotNull
    private String op;
    @NotNull
    private Object value;

    @NotNull
    public SqlOp plus(long val) {
        value = val;
        op = "+";
        return this;
    }

    @NotNull
    public SqlOp minus(long val) {
        value = val;
        op = "-";
        return this;
    }

    @NotNull
    public SqlOp multiply(long val) {
        value = val;
        op = "*";
        return this;
    }

    @NotNull

    public SqlOp divide(long val) {
        value = val;
        op = "/";
        return this;
    }

    @NotNull
    public SqlOp mod(long val) {
        value = val;
        op = "%";
        return this;
    }

    @NotNull
    public SqlOp plus(@NonNull BigDecimal val) {
        value = val;
        op = "+";
        return this;
    }

    @NotNull
    public SqlOp minus(@NonNull BigDecimal val) {
        value = val;
        op = "-";
        return this;
    }

    @NotNull
    public SqlOp multiply(@NonNull BigDecimal val) {
        value = val;
        op = "*";
        return this;
    }

    @NotNull
    public SqlOp divide(@NonNull BigDecimal val) {
        value = val;
        op = "/";
        return this;
    }

    @NotNull
    public SqlOp mod(@NonNull BigDecimal val) {
        value = val;
        op = "%";
        return this;
    }
}
