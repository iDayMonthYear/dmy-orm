package cn.com.idmy.orm.core;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface SqlFnExpr<T> {
    @NotNull
    SqlFn<T> apply();
}
