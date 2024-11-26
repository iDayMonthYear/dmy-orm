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

    @SuppressWarnings({"unchecked"})
    default Class<ID> idClass() {
        return (Class<ID>) ClassUtil.getTypeArgument(getClass(), 1);
    }

    @UpdateProvider(type = MybatisSqlProvider.class, method = updateBySql)
    int updateBySql(@Param(SUD) String sql, @Param(SQL_PARAMS) List<Object> params);

    @InsertProvider(type = MybatisSqlProvider.class, method = insert)
    int insert(@NonNull @Param(ENTITY) T entity);

    /**
     * 批量插入主键为自增时，不会回写到实体类。（需要查询回写，影响性能）
     */
    @InsertProvider(type = MybatisSqlProvider.class, method = inserts)
    int inserts(@NonNull @Param(ENTITIES) Collection<T> entities);

    @Nullable
    @SelectProvider(type = MybatisSqlProvider.class, method = get)
    T get(@NonNull @Param(SUD) Selects<T> select);

    @SelectProvider(type = MybatisSqlProvider.class, method = find)
    List<T> find(@NonNull @Param(SUD) Selects<T> select);

    @UpdateProvider(type = MybatisSqlProvider.class, method = update)
    int update(@NonNull @Param(SUD) Updates<T> update);

    @DeleteProvider(type = MybatisSqlProvider.class, method = delete)
    int delete(@NonNull @Param(SUD) Deletes<T> delete);

    @SelectProvider(type = MybatisSqlProvider.class, method = count)
    long count(@NonNull @Param(SUD) Selects<T> select);

    default int inserts(@NonNull Collection<T> entities, int size) {
        return MybatisDaoDelegate.inserts(this, entities, size);
    }

    default int insertOrUpdate(@NonNull T entity) {
        return MybatisDaoDelegate.insertOrUpdate(this, entity, true);
    }

    default int insertOrUpdate(@NonNull T entity, boolean ignoreNull) {
        return MybatisDaoDelegate.insertOrUpdate(this, entity, ignoreNull);
    }

    default int update(@NonNull T entity, boolean ignoreNull) {
        return MybatisDaoDelegate.update(this, entity, ignoreNull);
    }

    default int update(@NonNull T entity) {
        return update(entity, true);
    }

    default int delete(@NonNull ID id) {
        return MybatisDaoDelegate.delete(this, id);
    }

    default int delete(@NonNull Collection<ID> ids) {
        return MybatisDaoDelegate.delete(this, ids);
    }

    default <IN> Page<T> page(@NonNull Page<IN> pageIn, @NonNull Selects<T> select) {
        return MybatisDaoDelegate.page(this, pageIn, select);
    }

    @Nullable
    default T get(@NonNull ID id) {
        return MybatisDaoDelegate.get(this, id);
    }

    @Nullable
    default <R> R get(@NonNull ColumnGetter<T, R> col, @NonNull ID id) {
        return MybatisDaoDelegate.get(this, col, id);
    }

    @Nullable
    default <R> R get(@NonNull ColumnGetter<T, R> col, @NonNull Selects<T> select) {
        return MybatisDaoDelegate.get(this, col, select);
    }

    @SuppressWarnings({"unchecked"})
    @Nullable
    default T get(@NonNull Selects<T> chain, @NonNull ColumnGetter<T, ?>... cols) {
        return MybatisDaoDelegate.get(this, chain, cols);
    }

    default List<T> all() {
        return find(Selects.of(this));
    }

    default List<T> find(@NonNull Collection<ID> ids) {
        return MybatisDaoDelegate.find(this, ids);
    }

    default <R> List<R> find(@NonNull ColumnGetter<T, R> col, @NonNull Collection<ID> ids) {
        return MybatisDaoDelegate.find(this, col, ids);
    }

    default <R> List<R> find(@NonNull ColumnGetter<T, R> col, @NonNull Selects<T> chain) {
        return MybatisDaoDelegate.find(this, col, chain);
    }

    default boolean exists(@NonNull ID id) {
        return MybatisDaoDelegate.exists(this, id);
    }

    default boolean notExists(@NonNull ID id) {
        return !exists(id);
    }

    default boolean exists(@NonNull Selects<T> select) {
        return count(select) > 0;
    }

    default boolean notExists(@NonNull Selects<T> select) {
        return !exists(select);
    }

    @Nullable
    default <R extends Number> R sqlFn(@NonNull SqlFnName name, @NonNull ColumnGetter<T, R> col, @NonNull Selects<T> select) {
        return MybatisDaoDelegate.sqlFn(this, name, col, select);
    }

    @Nullable
    default <R extends Number> R sum(@NonNull ColumnGetter<T, R> col, @NonNull Selects<T> select) {
        return sqlFn(SqlFnName.SUM, col, select);
    }

    @Nullable
    default <R extends Number> R avg(@NonNull ColumnGetter<T, R> col, @NonNull Selects<T> select) {
        return sqlFn(SqlFnName.AVG, col, select);
    }

    @Nullable
    default <R extends Number> R min(@NonNull ColumnGetter<T, R> col, @NonNull Selects<T> select) {
        return sqlFn(SqlFnName.MIN, col, select);
    }

    @Nullable
    default <R extends Number> R max(@NonNull ColumnGetter<T, R> col, @NonNull Selects<T> select) {
        return sqlFn(SqlFnName.MAX, col, select);
    }

    @Nullable
    default <R extends Number> R abs(@NonNull ColumnGetter<T, R> col, @NonNull Selects<T> select) {
        return sqlFn(SqlFnName.ABS, col, select);
    }

    @SuppressWarnings({"unchecked"})
    default Map<ID, T> map(@NonNull ID... ids) {
        return MybatisDaoDelegate.map(this, ids);
    }

    default Map<ID, T> map(@NonNull Collection<ID> ids) {
        return MybatisDaoDelegate.map(this, ids);
    }

    default <R> Map<R, T> map(@NonNull ColumnGetter<T, R> col, @NonNull Selects<T> chain) {
        return CollStreamUtil.toIdentityMap(find(chain), col::get);
    }
}