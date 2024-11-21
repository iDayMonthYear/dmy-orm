package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.core.*;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import org.apache.ibatis.annotations.*;
import org.dromara.hutool.core.collection.CollStreamUtil;
import org.dromara.hutool.core.reflect.ClassUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.com.idmy.orm.mybatis.MybatisConsts.*;

public interface MybatisDao<T, ID> {
    @SuppressWarnings({"unchecked"})
    default Class<T> entityClass() {
        return (Class<T>) ClassUtil.getTypeArgument(getClass());
    }

    @InsertProvider(type = MybatisSqlProvider.class, method = INSERT)
    int insert(@NonNull @Param(ENTITY) T entity);

    /**
     * 批量插入主键为自增时，不会回写到实体类。（需要查询回写，影响性能）
     */
    @InsertProvider(type = MybatisSqlProvider.class, method = INSERTS)
    int inserts(@NonNull @Param(ENTITIES) Collection<T> entities);

    @Nullable
    @SelectProvider(type = MybatisSqlProvider.class, method = GET)
    T get(@NonNull @Param(CHAIN) SelectChain<T> select);

    @SelectProvider(type = MybatisSqlProvider.class, method = FIND)
    List<T> find(@NonNull @Param(CHAIN) SelectChain<T> select);

    @UpdateProvider(type = MybatisSqlProvider.class, method = UPDATE)
    int update(@NonNull @Param(CHAIN) UpdateChain<T> update);

    @DeleteProvider(type = MybatisSqlProvider.class, method = DELETE)
    int delete(@NonNull @Param(CHAIN) DeleteChain<T> delete);

    @SelectProvider(type = MybatisSqlProvider.class, method = COUNT)
    long count(@NonNull @Param(CHAIN) SelectChain<T> select);

    @Nullable
    default T get(@NonNull ID id) {
        return get(StringSelectChain.of(this).eq(TableManager.getIdName(entityClass()), id));
    }

    @Nullable
    default <R> R get(@NonNull ColumnGetter<T, R> getter, @NonNull ID id) {
        var chain = (StringSelectChain<T>) StringSelectChain.of(this).select(getter);
        chain.sqlParamsSize(1);
        chain.eq(TableManager.getIdName(entityClass()), id);
        T t = get(chain);
        return getter.get(t);
    }

    @Nullable
    default <R> R get(@NonNull ColumnGetter<T, R> getter, @NonNull SelectChain<T> chain) {
        if (chain.hasSelectColumn()) {
            throw new IllegalArgumentException("select ... from 中间不能有字段或者函数");
        }
        T t = get(chain.select(getter));
        return getter.get(t);
    }

    @SuppressWarnings({"unchecked"})
    @Nullable
    default T get(@NonNull SelectChain<T> chain, @NonNull ColumnGetter<T, ?>... getters) {
        if (chain.hasSelectColumn()) {
            throw new IllegalArgumentException("select ... from 中间不能有字段或者函数");
        }
        return get(chain.select(getters));
    }

    default List<T> all() {
        return find(SelectChain.of(this));
    }

    default List<T> find(@NonNull Collection<ID> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        } else {
            var chain = StringSelectChain.of(this);
            chain.sqlParamsSize(1);
            chain.in(TableManager.getIdName(entityClass()), ids);
            return find(chain);
        }
    }

    default <R> List<R> find(@NonNull ColumnGetter<T, R> getter, @NonNull Collection<ID> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        } else {
            var chain = (StringSelectChain<T>) StringSelectChain.of(this).select(getter);
            chain.sqlParamsSize(1);
            chain.in(TableManager.getIdName(entityClass()), ids);
            return find(chain).stream().map(getter::get).toList();
        }
    }

    default <R> List<R> find(@NonNull ColumnGetter<T, R> getter, @NonNull SelectChain<T> chain) {
        if (chain.hasSelectColumn()) {
            throw new IllegalArgumentException("select ... from 中间不能有字段或者函数");
        } else {
            var ts = find(chain.select(getter));
            return ts.stream().map(getter::get).toList();
        }
    }

    default boolean exists(@NonNull ID id) {
        var chain = StringSelectChain.of(this);
        chain.sqlParamsSize(1);
        chain.eq(TableManager.getIdName(entityClass()), id);
        return count(chain) > 0;
    }

    default boolean notExists(@NonNull ID id) {
        return !exists(id);
    }

    default boolean exists(@NonNull SelectChain<T> chain) {
        return count(chain) > 0;
    }

    default boolean notExists(@NonNull SelectChain<T> chain) {
        return !exists(chain);
    }

    default <R extends Number> R fn(@NonNull SqlFnName name, @NonNull ColumnGetter<T, R> getter, @NonNull SelectChain<T> chain) {
        if (chain.hasSelectColumn()) {
            throw new IllegalArgumentException("select ... from 中间不能有字段或者函数");
        } else if (name == SqlFnName.IF_NULL) {
            throw new IllegalArgumentException("不支持ifnull");
        } else {
            T t = get(chain.select(() -> new SqlFn<>(name, getter)));
            return getter.get(t);
        }
    }

    default <R extends Number> R sum(@NonNull ColumnGetter<T, R> getter, @NonNull SelectChain<T> chain) {
        return fn(SqlFnName.SUM, getter, chain);
    }

    default <R extends Number> R avg(@NonNull ColumnGetter<T, R> getter, @NonNull SelectChain<T> chain) {
        return fn(SqlFnName.AVG, getter, chain);
    }

    default <R extends Number> R min(@NonNull ColumnGetter<T, R> getter, @NonNull SelectChain<T> chain) {
        return fn(SqlFnName.MIN, getter, chain);
    }

    default <R extends Number> R max(@NonNull ColumnGetter<T, R> getter, @NonNull SelectChain<T> chain) {
        return fn(SqlFnName.MAX, getter, chain);
    }

    default <R extends Number> R abs(@NonNull ColumnGetter<T, R> getter, @NonNull SelectChain<T> chain) {
        return fn(SqlFnName.ABS, getter, chain);
    }

    @SuppressWarnings({"unchecked"})
    default Map<ID, T> map(@NonNull ID... ids) {
        if (ids.length == 0) {
            return Collections.emptyMap();
        } else {
            var chain = StringSelectChain.of(this);
            chain.sqlParamsSize(1);
            chain.in(TableManager.getIdName(entityClass()), (Object) ids);
            var entities = find(chain);
            return CollStreamUtil.toIdentityMap(entities, TableManager::getIdValue);
        }
    }

    default Map<ID, T> map(@NonNull Collection<ID> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        } else {
            var chain = StringSelectChain.of(this);
            chain.sqlParamsSize(1);
            chain.in(TableManager.getIdName(entityClass()), ids);
            return CollStreamUtil.toIdentityMap(find(chain), TableManager::getIdValue);
        }
    }

    default <R> Map<R, T> map(@NonNull ColumnGetter<T, R> getter, @NonNull SelectChain<T> chain) {
        return CollStreamUtil.toIdentityMap(find(chain), getter::get);
    }

    default int delete(@NonNull ID id) {
        var chain = StringDeleteChain.of(this);
        chain.sqlParamsSize(1);
        chain.eq(TableManager.getIdName(entityClass()), id);
        return delete(chain);
    }

    default int delete(@NonNull Collection<ID> ids) {
        if (ids.isEmpty()) {
            return 0;
        } else {
            var chain = StringDeleteChain.of(this);
            chain.sqlParamsSize(1);
            chain.in(TableManager.getIdName(entityClass()), ids);
            return delete(chain);
        }
    }
}