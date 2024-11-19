package cn.com.idmy.orm.core.mybatis;

import cn.com.idmy.orm.core.ast.DeleteChain;
import cn.com.idmy.orm.core.ast.SelectChain;
import cn.com.idmy.orm.core.ast.UpdateChain;
import jakarta.annotation.Nullable;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.dromara.hutool.core.reflect.TypeUtil;

import java.util.Collection;
import java.util.List;

import static cn.com.idmy.orm.core.mybatis.MybatisConsts.CHAIN;
import static cn.com.idmy.orm.core.mybatis.MybatisConsts.PRIMARY_VALUE;

public interface MybatisDao<T, ID> {
    default Class<T> entityType() {
        return (Class<T>) TypeUtil.getTypeArgument(getClass());
    }

    @Nullable
    @SelectProvider(type = MybatisSqlProvider.class, method = "get")
    T get(@Param(CHAIN) SelectChain<T> chain);

    @SelectProvider(type = MybatisSqlProvider.class, method = "find")
    List<T> find(@Param(CHAIN) SelectChain<T> chain);

    @UpdateProvider(type = MybatisSqlProvider.class, method = "update")
    int update(@Param(CHAIN) UpdateChain<T> chain);

    @DeleteProvider(type = MybatisSqlProvider.class, method = "delete")
    int delete(@Param(CHAIN) DeleteChain<T> chain);

    @SelectProvider(type = MybatisSqlProvider.class, method = "findByIds")
    List<T> findByIds(@Param(PRIMARY_VALUE) Collection<ID> ids);

    @Nullable
    @SelectProvider(type = MybatisSqlProvider.class, method = "getById")
    T getById(@Param(PRIMARY_VALUE) ID id);

    @DeleteProvider(type = MybatisSqlProvider.class, method = "deleteById")
    int deleteById(@Param(PRIMARY_VALUE) ID id);

    @DeleteProvider(type = MybatisSqlProvider.class, method = "deleteByIds")
    int deleteByIds(@Param(PRIMARY_VALUE) Collection<ID> ids);
}