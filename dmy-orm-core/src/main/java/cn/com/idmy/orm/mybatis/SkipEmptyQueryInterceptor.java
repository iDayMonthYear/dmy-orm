package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.core.SqlProvider;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.ArrayList;
import java.util.Map;

@Intercepts({@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class SkipEmptyQueryInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        var args = invocation.getArgs();
        var parameter = args[1];
        if (parameter instanceof Map<?, ?> map) {
            if (map.containsKey(SqlProvider.SKIP_EXECUTION)) {
                var ms = (MappedStatement) args[0];
                var boundSql = ms.getBoundSql(parameter);
                var sql = boundSql.getSql();
                if (SqlProvider.RETURN_NULL_SQL.equals(sql)) {
                    return null;
                } else if (SqlProvider.RETURN_EMPTY_LIST_SQL.equals(sql)) {
                    return new ArrayList<>();
                }
            }
        }
        return invocation.proceed();
    }
}