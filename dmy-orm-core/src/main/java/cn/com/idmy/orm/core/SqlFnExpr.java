package cn.com.idmy.orm.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface SqlFnExpr<T> {
    @NotNull
    SqlFn<T> apply(@Nullable SqlFn<T> in);
}