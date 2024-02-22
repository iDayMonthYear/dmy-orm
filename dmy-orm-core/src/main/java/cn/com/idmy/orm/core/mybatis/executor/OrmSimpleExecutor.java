package cn.com.idmy.orm.core.mybatis.executor;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.SimpleExecutor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

public class OrmSimpleExecutor extends SimpleExecutor implements CacheKeyBuilder {
    public OrmSimpleExecutor(Configuration configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    @Override
    public CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql) {
        return buildCacheKey(super.createCacheKey(ms, parameterObject, rowBounds, boundSql), parameterObject);
    }
}
