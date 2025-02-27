package cn.com.idmy.orm.core;


import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

@FunctionalInterface
public interface SqlOpExpr {
    @NotNull
    SqlOp<Number> op(@NonNull SqlOp<Number> col);
}