package cn.com.idmy.orm.core;


import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@FunctionalInterface
public interface SqlOpExpr extends Serializable {
    @NotNull SqlOp<Number> op(@NonNull SqlOp<Number> col);
}