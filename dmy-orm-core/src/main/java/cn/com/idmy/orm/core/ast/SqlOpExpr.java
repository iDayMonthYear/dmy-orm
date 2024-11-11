package cn.com.idmy.orm.core.ast;


@FunctionalInterface
public interface SqlOpExpr {
    SqlOp apply(SqlOp col);
}