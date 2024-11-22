package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Page;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import org.apache.ibatis.annotations.*;
import org.dromara.hutool.core.collection.CollStreamUtil;
import org.dromara.hutool.core.reflect.ClassUtil;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.com.idmy.orm.core.MybatisSqlProvider.*;

public interface MybatisDao<T, ID> {
    int DEFAULT_BATCH_SIZE = 1000;

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

    default int inserts(@NonNull Collection<T> entities, int size) {
        return MybatisSqlProvider.inserts(this, entities, size);
    }

    default int delete(@NonNull ID id) {
        return MybatisSqlProvider.delete(this, id);
    }

    default int delete(@NonNull Collection<ID> ids) {
        return MybatisSqlProvider.delete(this, ids);
    }

    default <IN> Page<T> page(@NonNull Page<IN> page, @NonNull SelectChain<T> select) {
        return MybatisSqlProvider.page(this, page, select);
    }

    @Nullable
    default T get(@NonNull ID id) {
        return MybatisSqlProvider.get(this, id);
    }

    @Nullable
    default <R> R get(@NonNull ColumnGetter<T, R> getter, @NonNull ID id) {
        return MybatisSqlProvider.get(this, getter, id);
    }

    @Nullable
    default <R> R get(@NonNull ColumnGetter<T, R> getter, @NonNull SelectChain<T> chain) {
        return MybatisSqlProvider.get(this, getter, chain);
    }

    @SuppressWarnings({"unchecked"})
    @Nullable
    default T get(@NonNull SelectChain<T> chain, @NonNull ColumnGetter<T, ?>... getters) {
        return MybatisSqlProvider.get(this, chain, getters);
    }

    default List<T> all() {
        return find(SelectChain.of(this));
    }

    default List<T> find(@NonNull Collection<ID> ids) {
        return MybatisSqlProvider.find(this, ids);
    }

    default <R> List<R> find(@NonNull ColumnGetter<T, R> getter, @NonNull Collection<ID> ids) {
        return MybatisSqlProvider.find(this, getter, ids);
    }

    default <R> List<R> find(@NonNull ColumnGetter<T, R> getter, @NonNull SelectChain<T> chain) {
        return MybatisSqlProvider.find(this, getter, chain);
    }

    default boolean exists(@NonNull ID id) {
        return MybatisSqlProvider.exists(this, id);
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

    @Nullable
    default <R extends Number> R fn(@NonNull SqlFnName name, @NonNull ColumnGetter<T, R> getter, @NonNull SelectChain<T> chain) {
        return MybatisSqlProvider.fn(this, name, getter, chain);
    }

    @Nullable
    default <R extends Number> R sum(@NonNull ColumnGetter<T, R> getter, @NonNull SelectChain<T> chain) {
        return fn(SqlFnName.SUM, getter, chain);
    }

    @Nullable
    default <R extends Number> R avg(@NonNull ColumnGetter<T, R> getter, @NonNull SelectChain<T> chain) {
        return fn(SqlFnName.AVG, getter, chain);
    }

    @Nullable
    default <R extends Number> R min(@NonNull ColumnGetter<T, R> getter, @NonNull SelectChain<T> chain) {
        return fn(SqlFnName.MIN, getter, chain);
    }

    @Nullable
    default <R extends Number> R max(@NonNull ColumnGetter<T, R> getter, @NonNull SelectChain<T> chain) {
        return fn(SqlFnName.MAX, getter, chain);
    }

    @Nullable
    default <R extends Number> R abs(@NonNull ColumnGetter<T, R> getter, @NonNull SelectChain<T> chain) {
        return fn(SqlFnName.ABS, getter, chain);
    }

    @SuppressWarnings({"unchecked"})
    default Map<ID, T> map(@NonNull ID... ids) {
        return MybatisSqlProvider.map(this, ids);
    }

    default Map<ID, T> map(@NonNull Collection<ID> ids) {
        return MybatisSqlProvider.map(this, ids);
    }

    default <R> Map<R, T> map(@NonNull ColumnGetter<T, R> getter, @NonNull SelectChain<T> chain) {
        return CollStreamUtil.toIdentityMap(find(chain), getter::get);
    }
}