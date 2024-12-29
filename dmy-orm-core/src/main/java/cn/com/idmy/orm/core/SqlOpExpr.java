package cn.com.idmy.orm.core;


import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface SqlOpExpr {
    @NotNull
    SqlOp<Number> apply(@NonNull SqlOp<Number> col);
}