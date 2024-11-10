package cn.com.idmy.orm.core.ast;


@FunctionalInterface
public interface SqlExpr {
    SqlExprFn apply(SqlExprFn col);
}