package cn.com.idmy.orm.core;

import jakarta.validation.constraints.NotNull;

public interface SqlFnExpr<T> {
    @NotNull
    SqlFn<T> get();
}