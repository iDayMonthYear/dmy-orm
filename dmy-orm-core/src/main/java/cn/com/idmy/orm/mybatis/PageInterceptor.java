package cn.com.idmy.orm.mybatis;

import cn.com.idmy.base.model.Page;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.util.Map;

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class PageInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        var statementHandler = (StatementHandler) invocation.getTarget();
        var metaObject = SystemMetaObject.forObject(statementHandler);
        var boundSql = statementHandler.getBoundSql();
        var parameterObject = boundSql.getParameterObject();
        var page = getPage(parameterObject);
        if (page == null) {
            return invocation.proceed();
        }
        var originalSql = boundSql.getSql();
        if (page.hasTotal() == null || page.hasTotal()) {
            var countSql = "SELECT COUNT(*) FROM (" + originalSql + ") tmp_count";
            var conn = (Connection) invocation.getArgs()[0];
            try (var st = conn.prepareStatement(countSql)) {
                statementHandler.getParameterHandler().setParameters(st);
                try (var rs = st.executeQuery()) {
                    if (rs.next()) {
                        page.total(rs.getLong(1));
                    }
                }
            }
        }
        metaObject.setValue("delegate.boundSql.sql", String.format("%s limit %d, %d", originalSql, page.offset(), page.pageSize()));
        return invocation.proceed();
    }

    private @Nullable Page<?> getPage(Object parameterObject) {
        switch (parameterObject) {
            case Page<?> page -> {
                return page;
            }
            case Map<?, ?> map -> {
                for (var value : map.values()) {
                    if (value instanceof Page<?> page) {
                        return page;
                    }
                }
            }
            case null, default -> {
                return null;
            }
        }
        return null;
    }
}