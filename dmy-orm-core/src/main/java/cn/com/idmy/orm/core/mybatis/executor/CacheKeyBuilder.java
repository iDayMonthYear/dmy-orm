package cn.com.idmy.orm.core.mybatis.executor;

import cn.com.idmy.orm.core.OrmConsts;
import org.apache.ibatis.cache.CacheKey;

import java.util.Arrays;
import java.util.Map;

public interface CacheKeyBuilder {
    default CacheKey buildCacheKey(CacheKey cacheKey, Object parameterObject) {
        if (parameterObject instanceof Map && ((Map<?, ?>) parameterObject).containsKey(OrmConsts.SQL_ARGS)) {
            cacheKey.update(Arrays.toString((Object[]) ((Map<?, ?>) parameterObject).get(OrmConsts.SQL_ARGS)));
        }
        return cacheKey;
    }
}
