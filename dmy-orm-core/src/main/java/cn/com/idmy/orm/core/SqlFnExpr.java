package cn.com.idmy.orm.core;


import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public interface SqlFnExpr<T> extends Serializable {
    @NotNull SqlFn<T> get();
}