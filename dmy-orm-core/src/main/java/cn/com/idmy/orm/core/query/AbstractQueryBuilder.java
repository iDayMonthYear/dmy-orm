package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.BaseMapper;
import lombok.RequiredArgsConstructor;

/**
 * 抽象关联查询。
 *
 * @author 王帅
 * @since 2023-08-08
 */
@RequiredArgsConstructor
public abstract class AbstractQueryBuilder<T> implements ChainQuery<T> {
    protected final MapperQueryChain<T> delegate;

    /**
     * @return BaseMapper
     */
    protected BaseMapper<T> baseMapper() {
        return delegate.baseMapper();
    }

    /**
     * @return QueryWrapper
     */
    protected QueryWrapper queryWrapper() {
        return delegate.toQueryWrapper();
    }
}
