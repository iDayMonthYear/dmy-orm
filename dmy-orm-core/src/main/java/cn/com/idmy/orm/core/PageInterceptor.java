package cn.com.idmy.orm.core;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class PageInterceptor implements Interceptor {
    @Override
    public @NotNull Object intercept(@NotNull Invocation invocation) throws Throwable {
        var statementHandler = (StatementHandler) invocation.getTarget();
        var boundSql = statementHandler.getBoundSql();
        var parameterObject = boundSql.getParameterObject();
        if (!(parameterObject instanceof XmlQuery<?> xmlQuery) || xmlQuery.page == null) {
            return invocation.proceed();
        }
        var originalSql = boundSql.getSql();
        if (xmlQuery.hasTotal == null || xmlQuery.hasTotal) {
            var countSql = getCountSql(originalSql);
            var conn = (Connection) invocation.getArgs()[0];
            try (var st = conn.prepareStatement(countSql)) {
                statementHandler.getParameterHandler().setParameters(st);
                try (var rs = st.executeQuery()) {
                    if (rs.next()) {
                        xmlQuery.total = rs.getLong(1);
                    }
                }
            }
        }
        var metaObject = SystemMetaObject.forObject(statementHandler);
        metaObject.setValue("delegate.boundSql.sql", String.format("%s limit %d, %d", originalSql, xmlQuery.offset(), xmlQuery.limit()));
        return invocation.proceed();
    }

    private static @NotNull String getCountSql(String originalSql) {
        return "select count(*) from (" + originalSql + ") tmp_count";
    }
}