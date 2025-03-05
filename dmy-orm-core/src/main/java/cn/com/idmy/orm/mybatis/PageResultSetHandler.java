package cn.com.idmy.orm.mybatis;

import cn.com.idmy.base.model.Page;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

public class PageResultSetHandler extends DefaultResultSetHandler {
    private final ParameterHandler parameterHandler;
    private final BoundSql boundSql;

    public PageResultSetHandler(Executor executor, MappedStatement mappedStatement, ParameterHandler parameterHandler, ResultHandler<?> resultHandler, BoundSql boundSql, RowBounds rowBounds) {
        super(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
        this.parameterHandler = parameterHandler;
        this.boundSql = boundSql;
    }

    @Override
    public List<Object> handleResultSets(Statement stmt) throws SQLException {
        List<Object> results = super.handleResultSets(stmt);
        // 获取参数对象
        Object parameterObject = parameterHandler.getParameterObject();

        // 如果参数是分页对象
        if (parameterObject instanceof Page) {
            Page page = (Page<?>) parameterObject;
            // 执行 count 查询
            // 设置分页信息
            page.total(executeCountQuery(stmt));
            page.rows(results);
            return Collections.singletonList(page);
        }
        return results;
    }

    private long executeCountQuery(Statement st) throws SQLException {
        // 构建 count 查询
        String countSql = "SELECT COUNT(*) FROM (" + boundSql.getSql() + ") tmp";

        try (var countStmt = st.getConnection().createStatement(); var rs = countStmt.executeQuery(countSql)) {
            return rs.next() ? rs.getLong(1) : 0L;
        }
    }
}