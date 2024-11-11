package cn.com.idmy.orm.core.ast;

@FunctionalInterface
public interface SqlFnExpr<T> {
    SqlFn<T> apply();
}
