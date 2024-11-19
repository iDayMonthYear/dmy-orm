package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.ast.DeleteChain;
import cn.com.idmy.orm.ast.SelectChain;
import cn.com.idmy.orm.ast.UpdateChain;
import jakarta.annotation.Nullable;
import org.apache.ibatis.annotations.*;
import org.dromara.hutool.core.reflect.TypeUtil;

import java.util.Collection;
import java.util.List;

import static cn.com.idmy.orm.mybatis.MybatisConsts.CHAIN;
import static cn.com.idmy.orm.mybatis.MybatisConsts.ENTITIES;
import static cn.com.idmy.orm.mybatis.MybatisConsts.ENTITY;
import static cn.com.idmy.orm.mybatis.MybatisConsts.PRIMARY_VALUE;

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

    @InsertProvider(type = MybatisSqlProvider.class, method = "insert")
    int insert(@Param(ENTITY) T entity);

    @InsertProvider(type = MybatisSqlProvider.class, method = "inserts")
    int inserts(@Param(ENTITIES) Collection<T> entities);

    @UpdateProvider(type = MybatisSqlProvider.class, method = "updateById")
    int updateById(@Param(ENTITY) T entity);
}