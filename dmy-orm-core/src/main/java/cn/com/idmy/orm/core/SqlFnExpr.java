package cn.com.idmy.orm.core;

@FunctionalInterface
public interface SqlFnExpr<T> {
    SqlFn<T> apply();
}
