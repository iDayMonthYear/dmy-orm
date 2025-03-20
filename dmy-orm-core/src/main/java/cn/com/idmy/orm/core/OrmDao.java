package cn.com.idmy.orm.core;

import cn.com.idmy.base.FieldGetter;
import cn.com.idmy.base.model.Page;
import cn.com.idmy.base.util.Assert;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.SqlNode.SqlCond;
import lombok.NonNull;
import org.apache.ibatis.annotations.*;
import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.collection.CollStreamUtil;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.reflect.TypeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.*;

public interface OrmDao<T, ID> {
    @SuppressWarnings("unchecked")
    default @NotNull Class<T> entityType() {
        return (Class<T>) TypeUtil.getTypeArgument(getClass());
    }

    @SuppressWarnings("unchecked")
    default @NotNull Class<ID> idType() {
        return (Class<ID>) TypeUtil.getTypeArgument(getClass(), 1);
    }

    default @NotNull TableInfo table() {
        return Tables.getTable(entityType());
    }

    default @NotNull Query<T> q() {
        return new Query<>(this, true);
    }

    default @NotNull Query<T> q(boolean nullable) {
        return new Query<>(this, nullable);
    }

    default @NotNull Update<T> u() {
        return new Update<>(entityType(), false);
    }

    default @NotNull Update<T> u(boolean nullable) {
        return new Update<>(entityType(), nullable);
    }

    default @NotNull Delete<T> d() {
        return new Delete<>(entityType(), false);
    }

    default @NotNull Delete<T> d(boolean nullable) {
        return new Delete<>(entityType(), nullable);
    }

    @SelectProvider(type = SqlProvider.class, method = SqlProvider.count)
    long count(@NotNull @Param(SqlProvider.CRUD) Query<T> q);

    default boolean has(@NotNull Query<T> q) {
        return count(q) > 0;
    }

    default boolean has(@NonNull ID id) {
        var q = q();
        q.sqlParamsSize = 1;
        q.addNode(new SqlCond(Tables.getIdColumnName(this), Op.EQ, id));
        return has(q);
    }

    default void has(@NonNull ID id, @NotNull String msg, @NotNull Object... params) {
        if (!has(id)) {
            throw new IllegalStateException(String.format(msg, params));
        }
    }

    default void has(@NotNull Query<T> q, @NotNull String msg, @NotNull Object... params) {
        if (!has(q)) {
            throw new IllegalStateException(String.format(msg, params));
        }
    }

    default boolean notHas(@NotNull Query<T> q) {
        return !has(q);
    }

    default boolean notHas(@NonNull ID id) {
        return !has(id);
    }

    default void notHas(@NonNull ID id, @NotNull String msg, @NotNull Object... params) {
        if (!notHas(id)) {
            throw new IllegalStateException(String.format(msg, params));
        }
    }

    default void notHas(@NotNull Query<T> q, @NotNull String msg, @NotNull Object... params) {
        if (!notHas(q)) {
            throw new IllegalStateException(String.format(msg, params));
        }
    }

    @SelectProvider(type = SqlProvider.class, method = SqlProvider.list0)
    @NotNull List<T> list0(@NotNull @Param(SqlProvider.CRUD) Query<T> q);

    default @NotNull List<T> list(@NotNull Query<T> q) {
        if (!q.hasCond && !q.hasAggregate && !q.force) {
            return new ArrayList<>(0);
        } else {
            return list0(q);
        }
    }

    default @NotNull List<T> list(@NotNull Query<T> q, @NotNull String msg, @NotNull Object... params) {
        return Assert.notEmpty(list(q), msg, params);
    }

    default @NotNull List<T> all() {
        return list(q().force());
    }

    default @NotNull List<T> list(@Nullable Collection<ID> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        } else {
            var q = q();
            q.sqlParamsSize = 1;
            q.addNode(new SqlCond(Tables.getIdColumnName(this), Op.IN, ids));
            return list(q);
        }
    }

    default @NotNull List<T> list(@NotNull Collection<ID> ids, @NotNull String msg, @NotNull Object... params) {
        return Assert.notEmpty(list(ids), msg, params);
    }

    default @NotNull <R> List<R> list(@NotNull FieldGetter<T, R> field, @Nullable Collection<ID> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        } else {
            var q = q().select(field);
            q.sqlParamsSize = 1;
            q.addNode(new SqlCond(Tables.getIdColumnName(this), Op.IN, ids));
            return list(q).stream().map(field::get).toList();
        }
    }

    default @NotNull <R> List<R> list(@NotNull FieldGetter<T, R> field, @NotNull Query<T> q) {
        SqlProvider.clearSelectColumns(q);
        var ts = list(q.select(field));
        return CollStreamUtil.toList(ts, field::get);
    }

    @SelectProvider(type = SqlProvider.class, method = SqlProvider.getNullable0)
    @Nullable T getNullable0(@NotNull @Param(SqlProvider.CRUD) Query<T> q);

    default @Nullable T getNullable(@NotNull Query<T> q) {
        if (!q.hasCond && !q.hasAggregate && !q.force) {
            return null;
        } else {
            return getNullable0(q);
        }
    }

    default @NotNull T get(@NotNull Query<T> q, @NotNull String msg, @NotNull Object... params) {
        return Assert.notNull(getNullable(q), msg, params);
    }

    default @NotNull T get(@NotNull Query<T> q) {
        return get(q, "根据「查询条件」找不到「{}」", Optional.ofNullable(table().title()).orElse(table().name()));
    }

    default @Nullable T getNullable(@NonNull ID id) {
        var q = q();
        q.sqlParamsSize = 1;
        q.addNode(new SqlCond(Tables.getIdColumnName(this), Op.EQ, id));
        return getNullable(q);
    }

    default @NotNull T get(@NonNull ID id) {
        return Assert.notNull(getNullable(id), "根据主键「{}」找不到「{}」", id, Optional.ofNullable(table().title()).orElse(table().name()));
    }

    default @Nullable <R> R getNullable(@NotNull FieldGetter<T, R> field, @NonNull ID id) {
        var q = q().select(field);
        q.sqlParamsSize = 1;
        q.addNode(new SqlCond(Tables.getIdColumnName(this), Op.EQ, id));
        T t = getNullable(q);
        return t == null ? null : field.get(t);
    }

    default @NotNull <R> R get(@NotNull FieldGetter<T, R> field, @NonNull ID id) {
        R r = getNullable(field, id);
        if (r == null) {
            var colum = Tables.getColum(entityType(), field);
            throw new OrmException("根据主键「{}」找不到「{}」", id, Optional.ofNullable(colum.title()).orElse(colum.name()));
        } else {
            return r;
        }
    }

    default @NotNull <R> R get(@NotNull FieldGetter<T, R> field, @NonNull ID id, @NotNull String msg, @NotNull Object... params) {
        return Assert.notNull(getNullable(field, id), msg, params);
    }

    default @Nullable <R> R getNullable(@NotNull FieldGetter<T, R> field, @NotNull Query<T> q) {
        SqlProvider.clearSelectColumns(q);
        q.select(field);
        T t = getNullable(q);
        return t == null ? null : field.get(t);
    }

    default @NotNull <R> R get(@NotNull FieldGetter<T, R> field, @NotNull Query<T> q, @NotNull String msg, @NotNull Object... params) {
        return Assert.notNull(getNullable(field, q), msg, params);
    }

    default @NotNull <R> R get(@NotNull FieldGetter<T, R> field, @NotNull Query<T> q) {
        R r = getNullable(field, q);
        if (r == null) {
            var col = Tables.getColum(entityType(), field);
            throw new OrmException("根据主键「查询条件」找不到「{}」", Optional.ofNullable(col.title()).orElse(col.name()));
        } else {
            return r;
        }
    }

    default @NotNull Map<ID, T> map(@Nullable ID[] ids) {
        return ArrayUtil.isEmpty(ids) ? Collections.emptyMap() : SqlProvider.map(this, ids);
    }

    default @NotNull Map<ID, T> map(@Nullable Collection<ID> ids) {
        return CollUtil.isEmpty(ids) ? Collections.emptyMap() : SqlProvider.map(this, ids);
    }

    default @NotNull <R> Map<R, T> map(@NotNull FieldGetter<T, R> key, @NotNull Query<T> q) {
        return CollStreamUtil.toIdentityMap(list(q), key::get);
    }

    default @NotNull <K, V> Map<K, V> map(@NotNull FieldGetter<T, K> key, @NotNull FieldGetter<T, V> val, @NotNull Query<T> q) {
        var list = list(q.select(key, val));
        var ret = new HashMap<K, V>(list.size());
        for (int i = 0, size = list.size(); i < size; i++) {
            T t = list.get(i);
            V value = val.get(t);
            if (value != null) {
                ret.put(key.get(t), value);
            }
        }
        return ret;
    }

    default @NotNull <IN> Page<T> page(@NonNull Page<IN> page, @NotNull Query<T> q) {
        return SqlProvider.page(this, page, q);
    }

    default @Nullable <R extends Number> R sqlFn(@NotNull SqlFnName name, @NotNull FieldGetter<T, R> field, @NotNull Query<T> q) {
        if (!q.force && !q.hasCond) {
            return null;
        } else if (name == SqlFnName.IF_NULL) {
            throw new OrmException("不支持ifnull");
        } else {
            SqlProvider.clearSelectColumns(q);
            T t = getNullable(q.one().select(() -> new SqlFn<>(name, field)));
            return t == null ? null : field.get(t);
        }
    }

    default @Nullable <R extends Number> R abs(@NotNull FieldGetter<T, R> field, @NotNull Query<T> q) {
        return sqlFn(SqlFnName.ABS, field, q);
    }

    default @Nullable <R extends Number> R avg(@NotNull FieldGetter<T, R> field, @NotNull Query<T> q) {
        return sqlFn(SqlFnName.AVG, field, q);
    }

    default @Nullable <R extends Number> R max(@NotNull FieldGetter<T, R> field, @NotNull Query<T> q) {
        return sqlFn(SqlFnName.MAX, field, q);
    }

    default @Nullable <R extends Number> R min(@NotNull FieldGetter<T, R> field, @NotNull Query<T> q) {
        return sqlFn(SqlFnName.MIN, field, q);
    }

    default @NotNull BigDecimal sum(@NotNull FieldGetter<T, BigDecimal> field, @NotNull Query<T> q) {
        var r = sqlFn(SqlFnName.SUM, field, q);
        return r == null ? BigDecimal.ZERO : r;
    }

    default long sumLong(@NotNull FieldGetter<T, Long> field, @NotNull Query<T> q) {
        var r = sqlFn(SqlFnName.SUM, field, q);
        return r == null ? 0 : r;
    }

    default int sumInt(@NotNull FieldGetter<T, Integer> field, @NotNull Query<T> q) {
        var r = sqlFn(SqlFnName.SUM, field, q);
        return r == null ? 0 : r;
    }

    @InsertProvider(type = SqlProvider.class, method = SqlProvider.create)
    int create(@NonNull @Param(SqlProvider.ENTITY) T entity);

    @InsertProvider(type = SqlProvider.class, method = SqlProvider.creates)
    int creates(@NonNull @Param(SqlProvider.ENTITIES) Collection<T> entities);

    default int creates(@Nullable Collection<T> entities, int size) {
        return SqlProvider.creates(this, entities, size);
    }

    default int createOrUpdate(@NonNull T entity, boolean ignoreNull) {
        ID idVal = Tables.getIdValue(entity);
        if (idVal == null) {
            return create(entity);
        } else {
            return has(idVal) ? SqlProvider.update(this, entity, ignoreNull) : create(entity);
        }
    }

    default int createOrUpdate(@NonNull T entity) {
        return createOrUpdate(entity, true);
    }

    @UpdateProvider(type = SqlProvider.class, method = SqlProvider.update)
    int update(@NotNull @Param(SqlProvider.CRUD) Update<T> update);

    default int update(@NonNull T entity) {
        return update(entity, true);
    }

    default int update(@NonNull T entity, boolean ignoreNull) {
        return SqlProvider.update(this, entity, ignoreNull);
    }

    default int[] update(@Nullable Collection<T> entities, int size, boolean ignoreNull) {
        if (entities == null) {
            return new int[]{0};
        } else {
            return SqlProvider.update(this, entities, size, ignoreNull);
        }
    }

    @UpdateProvider(type = SqlProvider.class, method = SqlProvider.updateBySql)
    int updateBySql(@NonNull @Param(SqlProvider.CRUD) String sql, @NonNull @Param(SqlProvider.SQL_PARAMS) List<Object> params);

    @DeleteProvider(type = SqlProvider.class, method = SqlProvider.delete)
    int delete(@NotNull @Param(SqlProvider.CRUD) Delete<T> d);

    default int delete(@NonNull ID id) {
        var d = d();
        d.sqlParamsSize = 1;
        d.addNode(new SqlCond(Tables.getIdColumnName(this), Op.EQ, id));
        return delete(d);
    }

    default int delete(@Nullable Collection<ID> ids) {
        if (CollUtil.isEmpty(ids)) {
            return -1;
        } else {
            var d = d();
            d.sqlParamsSize = 1;
            d.addNode(new SqlCond(Tables.getIdColumnName(this), Op.IN, ids));
            return delete(d);
        }
    }

    @NotNull
    default <E> XmlQuery<E> xml(@NotNull Page<E> page, boolean nullable) {
        return new XmlQuery<>(page, nullable);
    }

    @NotNull
    default <E> XmlQuery<E> xml(@NotNull Page<E> page) {
        return xml(page, true);
    }

    @NotNull
    default <E> XmlQuery<E> xml(@NotNull E params, boolean nullable) {
        @SuppressWarnings({"unchecked"})
        var outType = (Class<E>) params.getClass();
        var q = new XmlQuery<>(outType, nullable);
        q.params = params;
        return q;
    }

    @NotNull
    default <E> XmlQuery<E> xml(@NotNull E params) {
        return xml(params, true);
    }
}