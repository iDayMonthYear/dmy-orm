package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.BaseMapper;
import cn.com.idmy.orm.core.mybatis.Mappers;
import cn.com.idmy.orm.core.table.TableInfo;
import cn.com.idmy.orm.core.table.TableInfoFactory;

/**
 * {@link QueryWrapper} 链式调用。
 *
 * @author 王帅
 * @since 2023-07-22
 */
public class QueryChain<T> extends QueryWrapperAdapter<QueryChain<T>> implements MapperQueryChain<T> {

    private final BaseMapper<T> baseMapper;

    public QueryChain(BaseMapper<T> baseMapper) {
        this.baseMapper = baseMapper;
    }

    public static <T> QueryChain<T> of(Class<T> entityClass) {
        BaseMapper<T> baseMapper = Mappers.ofEntityClass(entityClass);
        return new QueryChain<>(baseMapper);
    }

    public static <E> QueryChain<E> of(BaseMapper<E> baseMapper) {
        return new QueryChain<>(baseMapper);
    }

    @Override
    public BaseMapper<T> baseMapper() {
        return baseMapper;
    }

    @Override
    public QueryWrapper toQueryWrapper() {
        return this;
    }

    @Override
    public String toSQL() {
        TableInfo tableInfo = TableInfoFactory.ofMapperClass(baseMapper.getClass());
        CPI.setFromIfNecessary(this, tableInfo.getSchema(), tableInfo.getTableName());
        return super.toSQL();
    }

}
