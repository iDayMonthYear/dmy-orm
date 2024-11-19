package cn.com.idmy.orm.ast;

@FunctionalInterface
public interface SqlFnExpr<T> {
    SqlFn<T> apply();
}
