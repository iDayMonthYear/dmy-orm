package cn.com.idmy.orm.core;


import org.jetbrains.annotations.NotNull;

public interface SqlFnExpr<T> {
    @NotNull SqlFn<T> get();
}