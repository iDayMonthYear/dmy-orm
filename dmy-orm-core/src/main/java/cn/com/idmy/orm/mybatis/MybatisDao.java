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
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        } else {
            var chain = StringSelectChain.of(this);
            chain.sqlParamsSize(1);
            chain.in(OrmUtil.getId(entityClass()), ids);
            return find(chain);
        }
    }

    default <R> List<R> find(Collection<ID> ids, FieldGetter<T, R> getter) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        } else {
            var chain = (StringSelectChain<T>) StringSelectChain.of(this).select(getter);
            chain.sqlParamsSize(1);
            chain.in(OrmUtil.getId(entityClass()), ids);
            return find(chain).stream().map(getter::get).toList();
        }
    }

    default <R> List<R> find(SelectChain<T> chain, FieldGetter<T, R> getter) {
        if (chain.hasSelectField()) {
            throw new IllegalArgumentException("select ... from 中间不能有字段或者函数");
        } else {
            List<T> ts = find(chain.select(getter));
            return ts.stream().map(getter::get).toList();
        }
    }

    @Nullable
    default T get(ID id) {
        return get(StringSelectChain.of(this).eq(OrmUtil.getId(entityClass()), id));
    }

    @Nullable
    default <R> R get(ID id, FieldGetter<T, R> getter) {
        var chain = (StringSelectChain<T>) StringSelectChain.of(this).select(getter);
        chain.sqlParamsSize(1);
        chain.eq(OrmUtil.getId(entityClass()), id);
        T t = get(chain);
        return Optional.ofNullable(t).map(getter::get).orElse(null);
    }

    @Nullable
    default <R> R get(SelectChain<T> chain, FieldGetter<T, R> getter) {
        if (chain.hasSelectField()) {
            throw new IllegalArgumentException("select ... from 中间不能有字段或者函数");
        }
        T t = get(chain.select(getter));
        if (t == null) {
            return null;
        } else {
            return getter.get(t);
        }
    }

    default long count(SelectChain<T> chain) {
        if (chain.hasSelectField()) {
            throw new IllegalArgumentException("select ... from 中间不能有字段或者函数");
        } else {
            T t = get(chain.select(SqlFn::count));
            return (long) t;
        }
    }

    default boolean exists(ID id) {
        var chain = StringSelectChain.of(this);
        chain.sqlParamsSize(1);
        chain.eq(OrmUtil.getId(entityClass()), id);
        return count(chain) > 0;
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
        if (chain.hasSelectField()) {
            throw new IllegalArgumentException("select ... from 中间不能有字段或者函数");
        } else {
            T t = get(chain.select(() -> SqlFn.sum(getter)));
            return getter.get(t);
        }
    }

    default int sumInt(SelectChain<T> chain, FieldGetter<T, Integer> getter) {
        if (chain.hasSelectField()) {
            throw new IllegalArgumentException("select ... from 中间不能有字段或者函数");
        } else {
            T t = get(chain.select(() -> SqlFn.sum(getter)));
            return getter.get(t);
        }
    }

    default BigDecimal sumBigDecimal(SelectChain<T> chain, FieldGetter<T, BigDecimal> getter) {
        if (chain.hasSelectField()) {
            throw new IllegalArgumentException("select ... from 中间不能有字段或者函数");
        } else {
            T t = get(chain.select(() -> SqlFn.sum(getter)));
            return getter.get(t);
        }
    }

    default Map<ID, T> map(ID... ids) {
        return null;
    }

    default int delete(ID id) {
        var chain = StringDeleteChain.of(this);
        chain.sqlParamsSize(1);
        chain.eq(OrmUtil.getId(entityClass()), id);
        return delete(chain);
    }

    default int delete(Collection<ID> ids) {
        if (CollUtil.isEmpty(ids)) {
            return -1;
        } else {
            var chain = StringDeleteChain.of(this);
            chain.sqlParamsSize(1);
            chain.in(OrmUtil.getId(entityClass()), ids);
            return delete(chain);
        }
    }
}