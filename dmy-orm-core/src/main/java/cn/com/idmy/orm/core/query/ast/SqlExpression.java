package cn.com.idmy.orm.core.query.ast;


import cn.com.idmy.orm.core.query.fn.SqlExpressionFn;

@FunctionalInterface
public interface SqlExpression {
    SqlExpressionFn apply(SqlExpressionFn col);
}