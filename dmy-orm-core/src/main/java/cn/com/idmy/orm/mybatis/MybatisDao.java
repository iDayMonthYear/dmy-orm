package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.core.*;
import cn.com.idmy.orm.util.OrmUtil;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import org.apache.ibatis.annotations.*;
import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.reflect.ClassUtil;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.com.idmy.orm.mybatis.MybatisConsts.CHAIN;
import static cn.com.idmy.orm.mybatis.MybatisConsts.ENTITIES;
import static cn.com.idmy.orm.mybatis.MybatisConsts.ENTITY;

public interface MybatisDao<T, ID> {
    default Class<T> entityClass() {
        return (Class<T>) ClassUtil.getTypeArgument(getClass());
    }

    @Nullable
    @SelectProvider(type = MybatisSqlProvider.class, method = "get")
    T get(@NonNull @Param(CHAIN) SelectChain<T> select);

    @SelectProvider(type = MybatisSqlProvider.class, method = "find")
    List<T> find(@NonNull @Param(CHAIN) SelectChain<T> select);

    @UpdateProvider(type = MybatisSqlProvider.class, method = "update")
    int update(@NonNull @Param(CHAIN) UpdateChain<T> update);

    @DeleteProvider(type = MybatisSqlProvider.class, method = "delete")
    int delete(@NonNull @Param(CHAIN) DeleteChain<T> delete);

    @InsertProvider(type = MybatisSqlProvider.class, method = "insert")
    int insert(@NonNull @Param(ENTITY) T entity);

    @InsertProvider(type = MybatisSqlProvider.class, method = "inserts")
    @Options(useGeneratedKeys = true, keyProperty = PLACEHOLDER)
    int inserts(@NonNull @Param(ENTITIES) Collection<T> entities);

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
            var ts = find(chain.select(getter));
            return ts.stream().map(getter::get).toList();
        }
    }

    @Nullable
    default T get(@NonNull ID id) {
        return get(StringSelectChain.of(this).eq(OrmUtil.getId(entityClass()), id));
    }

    @Nullable
    default <R> R get(@NonNull ID id, @NonNull FieldGetter<T, R> getter) {
        var chain = (StringSelectChain<T>) StringSelectChain.of(this).select(getter);
        chain.sqlParamsSize(1);
        chain.eq(OrmUtil.getId(entityClass()), id);
        T t = get(chain);
        return Optional.ofNullable(t).map(getter::get).orElse(null);
    }

    @Nullable
    default <R> R get(@NonNull SelectChain<T> chain, @NonNull FieldGetter<T, R> getter) {
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

    default long count(@NonNull SelectChain<T> chain) {
        if (chain.hasSelectField()) {
            throw new IllegalArgumentException("select ... from 中间不能有字段或者函数");
        } else {
            T t = get(chain.select(SqlFn::count));
            return (long) t;
        }
    }

    default boolean exists(@NonNull ID id) {
        var chain = StringSelectChain.of(this);
        chain.sqlParamsSize(1);
        chain.eq(OrmUtil.getId(entityClass()), id);
        return count(chain) > 0;
    }

    default boolean notExist(@NonNull ID id) {
        return !exists(id);
    }

    default boolean exists(SelectChain<T> chain) {
        return count(chain) > 0;
    }

    default boolean notExist(@NonNull SelectChain<T> chain) {
        return !exists(chain);
    }

    default long sumLong(@NonNull SelectChain<T> chain, @NonNull FieldGetter<T, Long> getter) {
        if (chain.hasSelectField()) {
            throw new IllegalArgumentException("select ... from 中间不能有字段或者函数");
        } else {
            T t = get(chain.select(() -> SqlFn.sum(getter)));
            return getter.get(t);
        }
    }

    default int sumInt(@NonNull SelectChain<T> chain, @NonNull FieldGetter<T, Integer> getter) {
        if (chain.hasSelectField()) {
            throw new IllegalArgumentException("select ... from 中间不能有字段或者函数");
        } else {
            T t = get(chain.select(() -> SqlFn.sum(getter)));
            return getter.get(t);
        }
    }

    default BigDecimal sumBigDecimal(@NonNull SelectChain<T> chain, @NonNull FieldGetter<T, BigDecimal> getter) {
        if (chain.hasSelectField()) {
            throw new IllegalArgumentException("select ... from 中间不能有字段或者函数");
        } else {
            T t = get(chain.select(() -> SqlFn.sum(getter)));
            return getter.get(t);
        }
    }

    default Map<ID, T> map(@NonNull ID... ids) {
        if (ArrayUtil.isEmpty(ids)) {
            return Collections.emptyMap();
        } else {
            var id = OrmUtil.getId(entityClass());
            var chain = StringSelectChain.of(this);
            chain.sqlParamsSize(1);
            chain.in(id, ids);
            var entities = find(chain);
            return entities.stream().collect(Collectors.toMap(entity -> (ID) OrmUtil.getIdValue(entity), Function.identity()));
        }
    }

    default Map<ID, T> map(@NonNull Collection<ID> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyMap();
        } else {
            var id = OrmUtil.getId(entityClass());
            var chain = StringSelectChain.of(this);
            chain.sqlParamsSize(1);
            chain.in(id, ids);
            var entities = find(chain);
            return entities.stream().collect(Collectors.toMap(entity -> (ID) OrmUtil.getIdValue(entity), Function.identity()));
        }
    }

    default int delete(@NonNull ID id) {
        var chain = StringDeleteChain.of(this);
        chain.sqlParamsSize(1);
        chain.eq(OrmUtil.getId(entityClass()), id);
        return delete(chain);
    }

    default int delete(@NonNull Collection<ID> ids) {
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