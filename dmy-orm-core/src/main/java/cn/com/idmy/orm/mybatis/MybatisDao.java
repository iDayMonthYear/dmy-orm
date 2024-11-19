package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.core.*;
import cn.com.idmy.orm.util.OrmUtil;
import jakarta.annotation.Nullable;
import org.apache.ibatis.annotations.*;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.reflect.ClassUtil;

import java.math.BigDecimal;
import java.util.*;

import static cn.com.idmy.orm.mybatis.MybatisConsts.CHAIN;
import static cn.com.idmy.orm.mybatis.MybatisConsts.ENTITIES;
import static cn.com.idmy.orm.mybatis.MybatisConsts.ENTITY;

public interface MybatisDao<T, ID> {
    default Class<T> entityClass() {
        return (Class<T>) ClassUtil.getTypeArgument(getClass());
    }

    @Nullable
    @SelectProvider(type = MybatisSqlProvider.class, method = "get")
    T get(@Param(CHAIN) SelectChain<T> select);

    @SelectProvider(type = MybatisSqlProvider.class, method = "find")
    List<T> find(@Param(CHAIN) SelectChain<T> select);

    @UpdateProvider(type = MybatisSqlProvider.class, method = "update")
    int update(@Param(CHAIN) UpdateChain<T> update);

    @DeleteProvider(type = MybatisSqlProvider.class, method = "delete")
    int delete(@Param(CHAIN) DeleteChain<T> delete);

    @InsertProvider(type = MybatisSqlProvider.class, method = "insert")
    int insert(@Param(ENTITY) T entity);

    @InsertProvider(type = MybatisSqlProvider.class, method = "inserts")
    int inserts(@Param(ENTITIES) Collection<T> entities);

    default List<T> all() {
        return find(SelectChain.of(this));
    }

    default List<T> find(Collection<ID> ids) {
        return CollUtil.isEmpty(ids) ? Collections.emptyList() : find(StringSelectChain.of(this).in(OrmUtil.getId(entityClass()), ids));
    }

    default <R> List<R> find(Collection<ID> ids, FieldGetter<T, R> getter) {
        var select = (StringSelectChain<T>) StringSelectChain.of(this).select(getter);
        var chain = select.in(OrmUtil.getId(entityClass()), ids);
        return find(chain).stream().map(getter::get).toList();
    }

    default <R> List<R> find(SelectChain<T> chain, FieldGetter<T, R> getter) {
        List<T> ts = find(chain.select(getter));
        return ts.stream().map(getter::get).toList();
    }

    @Nullable
    default T get(ID id) {
        return get(StringSelectChain.of(this).eq(OrmUtil.getId(entityClass()), id));
    }

    @Nullable
    default <R> R get(ID id, FieldGetter<T, R> getter) {
        var select = (StringSelectChain<T>) StringSelectChain.of(this).select(getter);
        T t = get(select.eq(OrmUtil.getId(entityClass()), id));
        return Optional.ofNullable(t).map(getter::get).orElse(null);
    }

    default long count(SelectChain<T> chain) {
        T t = get(chain.select(SqlFn::count));
        return (long) t;
    }

    default boolean exists(ID id) {
        return count(StringSelectChain.of(this).eq(OrmUtil.getId(entityClass()), id)) > 0;
    }

    default boolean notExist(ID id) {
        return !exists(id);
    }

    default boolean exists(SelectChain<T> chain) {
        return count(chain) > 0;
    }

    default boolean notExist(SelectChain<T> chain) {
        return !exists(chain);
    }

    default long sumLong(SelectChain<T> chain, FieldGetter<T, Long> getter) {
        T t = get(chain.select(() -> SqlFn.sum(getter)));
        return getter.get(t);
    }

    default int sumInt(SelectChain<T> chain, FieldGetter<T, Integer> getter) {
        T t = get(chain.select(() -> SqlFn.sum(getter)));
        return getter.get(t);
    }

    default BigDecimal sumBigDecimal(SelectChain<T> chain, FieldGetter<T, BigDecimal> getter) {
        T t = get(chain.select(() -> SqlFn.sum(getter)));
        return getter.get(t);
    }

    default Map<ID, T> map(ID... ids) {
        return null;
    }

    default int delete(ID id) {
        return delete(StringDeleteChain.of(this).eq(OrmUtil.getId(entityClass()), id));
    }

    default int delete(Collection<ID> ids) {
        return CollUtil.isEmpty(ids) ? -1 : delete(StringDeleteChain.of(this).in(OrmUtil.getId(entityClass()), ids));
    }
}