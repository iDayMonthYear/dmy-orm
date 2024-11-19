package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.ast.DeleteChain;
import cn.com.idmy.orm.ast.SelectChain;
import cn.com.idmy.orm.ast.UpdateChain;
import cn.com.idmy.orm.util.OrmUtil;
import jakarta.annotation.Nullable;
import org.apache.ibatis.annotations.*;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.reflect.ClassUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cn.com.idmy.orm.ast.StringSelectChain.of;
import static cn.com.idmy.orm.mybatis.MybatisConsts.CHAIN;
import static cn.com.idmy.orm.mybatis.MybatisConsts.ENTITIES;
import static cn.com.idmy.orm.mybatis.MybatisConsts.ENTITY;

public interface MybatisDao<T, ID> {
    default Class<T> entityType() {
        return (Class<T>) ClassUtil.getTypeArgument(getClass());
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

    @InsertProvider(type = MybatisSqlProvider.class, method = "insert")
    int insert(@Param(ENTITY) T entity);

    @InsertProvider(type = MybatisSqlProvider.class, method = "inserts")
    int inserts(@Param(ENTITIES) Collection<T> entities);

    default List<T> find(Collection<ID> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        } else {
            return find(of(this).in(OrmUtil.getPrimaryKey(entityType()), ids));
        }
    }

    @Nullable
    default T get(ID id) {
        return get(of(this).eq(OrmUtil.getPrimaryKey(entityType()), id));
    }

    default int delete(ID id) {
        return 0;
    }

    default int delete(Collection<ID> ids) {
        return 0;
    }

    default int update(T entity) {
        return 0;
    }
}