package cn.com.idmy.orm.ast;


@FunctionalInterface
public interface SqlOpExpr {
    SqlOp apply(SqlOp col);
}