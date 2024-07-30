package cn.com.idmy.orm.core.mybatis;

import lombok.experimental.Delegate;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.statement.CallableStatementHandler;
import org.apache.ibatis.executor.statement.SimpleStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

public class OrmStatementHandler implements StatementHandler {
    @Delegate
    private final org.apache.ibatis.executor.statement.StatementHandler delegate;

    public OrmStatementHandler(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        switch (ms.getStatementType()) {
            case STATEMENT ->
                    delegate = new SimpleStatementHandler(executor, ms, parameter, rowBounds, resultHandler, boundSql);
            case PREPARED ->
                    delegate = new OrmPreparedStatementHandler(executor, ms, parameter, rowBounds, resultHandler, boundSql);
            case CALLABLE ->
                    delegate = new CallableStatementHandler(executor, ms, parameter, rowBounds, resultHandler, boundSql);
            default -> throw new ExecutorException("Unknown statement type: " + ms.getStatementType());
        }
    }
}
