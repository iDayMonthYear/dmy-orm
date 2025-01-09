package cn.com.idmy.orm.core;


import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface SqlOpExpr {
    @NotNull
    SqlOp<Number> op(@NonNull SqlOp<Number> col);
}