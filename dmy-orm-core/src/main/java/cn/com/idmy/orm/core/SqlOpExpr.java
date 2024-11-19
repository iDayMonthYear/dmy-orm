package cn.com.idmy.orm.core;


@FunctionalInterface
public interface SqlOpExpr {
    SqlOp apply(SqlOp col);
}