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
import org.dromara.hutool.core.convert.ConvertUtil;
import org.dromara.hutool.core.reflect.ClassUtil;
import org.dromara.hutool.core.reflect.TypeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface OrmDao<T, ID> {
    @NotNull
    @SuppressWarnings("unchecked")
    default Class<T> entityType() {
        return (Class<T>) TypeUtil.getTypeArgument(getClass());
    }

    @NotNull
    @SuppressWarnings("unchecked")
    default Class<ID> idType() {
        return (Class<ID>) TypeUtil.getTypeArgument(getClass(), 1);
    }

    @NotNull
    default TableInfo table() {
        return Tables.getTable(entityType());
    }

    @NotNull
    default Query<T, ID> q() {
        return Query.of(this, true);
    }

    @NotNull
    default Query<T, ID> q(boolean nullable) {
        return Query.of(this, nullable);
    }

    @NotNull
    default Update<T, ID> u() {
        return Update.of(this, false);
    }

    @NotNull
    default Update<T, ID> u(boolean nullable) {
        return Update.of(this, nullable);
    }

    @NotNull
    default Delete<T, ID> d() {
        return Delete.of(this, false);
    }

    @NotNull
    default Delete<T, ID> d(boolean nullable) {
        return Delete.of(this, nullable);
    }

    @SelectProvider(type = SqlProvider.class, method = SqlProvider.count)
    long count(@NotNull @Param(SqlProvider.CRUD) Query<T, ID> q);

    default boolean exists(@NotNull Query<T, ID> q) {
        return count(q) > 0;
    }

    default boolean exists(@NonNull ID id) {
        var q = q();
        q.sqlParamsSize = 1;
        q.addNode(new SqlCond(Tables.getIdName(this), Op.EQ, id));
        return exists(q);
    }

    default void exists(@NonNull ID id, @NotNull String msg, @NotNull Object... params) {
        if (!exists(id)) {
            throw new IllegalStateException(String.format(msg, params));
        }
    }

    default void exists(@NotNull Query<T, ID> q, @NotNull String msg, @NotNull Object... params) {
        if (!exists(q)) {
            throw new IllegalStateException(String.format(msg, params));
        }
    }

    default boolean notExists(@NotNull Query<T, ID> q) {
        return !exists(q);
    }

    default boolean notExists(@NonNull ID id) {
        return !exists(id);
    }

    default void notExists(@NonNull ID id, @NotNull String msg, @NotNull Object... params) {
        if (!notExists(id)) {
            throw new IllegalStateException(String.format(msg, params));
        }
    }

    default void notExists(@NotNull Query<T, ID> q, @NotNull String msg, @NotNull Object... params) {
        if (!notExists(q)) {
            throw new IllegalStateException(String.format(msg, params));
        }
    }

    @Nullable
    @SelectProvider(type = SqlProvider.class, method = SqlProvider.list0)
    List<T> list0(@NotNull @Param(SqlProvider.CRUD) Query<T, ID> q);

    @NotNull
    default List<T> list(@NotNull Query<T, ID> q) {
        List<T> ts = list0(q);
        return ts == null ? new ArrayList<>(0) : ts;
    }

    @NotNull
    default List<T> list(@NotNull Query<T, ID> q, @NotNull String msg, @NotNull Object... params) {
        return Assert.notEmpty(list(q), msg, params);
    }

    @NotNull
    default List<T> all() {
        Query<T, ID> q = q();
        q.force = true;
        return list(q);
    }

    @NotNull
    default List<T> list(@Nullable Collection<ID> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        } else {
            var q = q();
            q.sqlParamsSize = 1;
            q.addNode(new SqlCond(Tables.getIdName(this), Op.IN, ids));
            return list(q);
        }
    }

    @NotNull
    default List<T> list(@NotNull Collection<ID> ids, @NotNull String msg, @NotNull Object... params) {
        return Assert.notEmpty(list(ids), msg, params);
    }

    @NotNull
    default <R> List<R> list(@NotNull FieldGetter<T, R> field, @Nullable Collection<ID> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        } else {
            var q = q().select(field);
            q.sqlParamsSize = 1;
            q.addNode(new SqlCond(Tables.getIdName(this), Op.IN, ids));
            return list(q).stream().map(field::get).toList();
        }
    }

    @NotNull
    default <R> List<R> list(@NotNull FieldGetter<T, R> field, @NotNull Query<T, ID> q) {
        SqlProvider.clearSelectColumns(q);
        var ts = list(q.select(field));
        return CollStreamUtil.toList(ts, field::get);
    }

    @NotNull
    default List<T> list(@NotNull Query<T, ID> q, @NotNull FieldGetter<T, ?> field, @NotNull FieldGetter<T, ?>... fields) {
        q.select(field);
        q.select(fields);
        return list(q);
    }

    @Nullable
    @SelectProvider(type = SqlProvider.class, method = SqlProvider.getNullable)
    T getNullable(@NotNull @Param(SqlProvider.CRUD) Query<T, ID> q);

    @NotNull
    default T get(@NotNull Query<T, ID> q, @NotNull String msg, @NotNull Object... params) {
        return Assert.notNull(getNullable(q), msg, params);
    }

    @NotNull
    default T get(@NotNull Query<T, ID> q) {
        return get(q, "根据「查询条件」找不到「{}」", Optional.ofNullable(table().title()).orElse(table().name()));
    }

    @Nullable
    default T getNullable(@NonNull ID id) {
        var q = q();
        q.sqlParamsSize = 1;
        q.addNode(new SqlCond(Tables.getIdName(this), Op.EQ, id));
        return getNullable(q);
    }

    @NotNull
    default T get(@NonNull ID id) {
        return Assert.notNull(getNullable(id), "根据主键「{}」找不到「{}」", id, Optional.ofNullable(table().title()).orElse(table().name()));
    }

    @Nullable
    default <R> R getNullable(@NotNull FieldGetter<T, R> field, @NonNull ID id) {
        var q = q().select(field);
        q.sqlParamsSize = 1;
        q.addNode(new SqlCond(Tables.getIdName(this), Op.EQ, id));
        T t = getNullable(q);
        return t == null ? null : field.get(t);
    }

    @NotNull
    default <R> R get(@NotNull FieldGetter<T, R> field, @NonNull ID id) {
        R r = getNullable(field, id);
        if (r == null) {
            var colum = Tables.getColum(entityType(), field);
            throw new OrmException("根据主键「{}」找不到「{}」", id, Optional.ofNullable(colum.title()).orElse(colum.name()));
        } else {
            return r;
        }
    }

    @NotNull
    default <R> R get(@NotNull FieldGetter<T, R> field, @NonNull ID id, @NotNull String msg, @NotNull Object... params) {
        return Assert.notNull(getNullable(field, id), msg, params);
    }

    @Nullable
    default <R> R getNullable(@NotNull FieldGetter<T, R> field, @NotNull Query<T, ID> q) {
        SqlProvider.clearSelectColumns(q);
        q.select(field);
        T t = getNullable(q);
        return t == null ? null : field.get(t);
    }

    @NotNull
    default <R> R get(@NotNull FieldGetter<T, R> field, @NotNull Query<T, ID> q, @NotNull String msg, @NotNull Object... params) {
        return Assert.notNull(getNullable(field, q), msg, params);
    }

    @NotNull
    default <R> R get(@NotNull FieldGetter<T, R> field, @NotNull Query<T, ID> q) {
        R r = getNullable(field, q);
        if (r == null) {
            var col = Tables.getColum(entityType(), field);
            throw new OrmException("根据主键「查询条件」找不到「{}」", Optional.ofNullable(col.title()).orElse(col.name()));
        } else {
            return r;
        }
    }

    @Nullable
    default T getNullable(@NotNull Query<T, ID> q, @NotNull FieldGetter<T, ?> field, @NotNull FieldGetter<T, ?>... fields) {
        SqlProvider.clearSelectColumns(q);
        q.select(field);
        q.select(fields);
        return getNullable(q);
    }

    @NotNull
    default T get(@NotNull Query<T, ID> q, @NotNull FieldGetter<T, ?> field, @NotNull FieldGetter<T, ?>... fields) {
        SqlProvider.clearSelectColumns(q);
        q.select(field);
        q.select(fields);
        return get(q);
    }

    @NotNull
    default Map<ID, T> map(@Nullable ID... ids) {
        return ArrayUtil.isEmpty(ids) ? Collections.emptyMap() : SqlProvider.map(this, ids);
    }

    @NotNull
    default Map<ID, T> map(@Nullable Collection<ID> ids) {
        return CollUtil.isEmpty(ids) ? Collections.emptyMap() : SqlProvider.map(this, ids);
    }

    @NotNull
    default <R> Map<R, T> map(@NotNull FieldGetter<T, R> field, @NotNull Query<T, ID> q) {
        return CollStreamUtil.toIdentityMap(list(q), field::get);
    }

    @NotNull
    default <IN> Page<T> page(@NonNull Page<IN> page, @NotNull Query<T, ID> q) {
        return SqlProvider.page(this, page, q);
    }

    @Nullable
    default <R extends Number> R sqlFn(@NotNull SqlFnName name, @NotNull FieldGetter<T, R> field, @NotNull Query<T, ID> q) {
        if (name == SqlFnName.IF_NULL) {
            throw new OrmException("不支持ifnull");
        } else {
            SqlProvider.clearSelectColumns(q);
            q.limit = 1;
            T t = getNullable(q.select(() -> new SqlFn<>(name, field)));
            return t == null ? null : field.get(t);
        }
    }

    @Nullable
    default <R extends Number> R abs(@NotNull FieldGetter<T, R> field, @NotNull Query<T, ID> q) {
        return sqlFn(SqlFnName.ABS, field, q);
    }

    @Nullable
    default <R extends Number> R avg(@NotNull FieldGetter<T, R> field, @NotNull Query<T, ID> q) {
        return sqlFn(SqlFnName.AVG, field, q);
    }

    @Nullable
    default <R extends Number> R max(@NotNull FieldGetter<T, R> field, @NotNull Query<T, ID> q) {
        return sqlFn(SqlFnName.MAX, field, q);
    }

    @Nullable
    default <R extends Number> R min(@NotNull FieldGetter<T, R> field, @NotNull Query<T, ID> q) {
        return sqlFn(SqlFnName.MIN, field, q);
    }

    @NotNull
    default <R extends Number> R sum(@NotNull FieldGetter<T, R> field, @NotNull Query<T, ID> q) {
        R result = sqlFn(SqlFnName.SUM, field, q);
        if (result == null) {
            var fieldType = ClassUtil.getTypeArgument(field.getClass());
            @SuppressWarnings("unchecked") R zero = (R) ConvertUtil.convert(fieldType, 0);
            return zero;
        } else {
            return result;
        }
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
            return exists(idVal) ? SqlProvider.update(this, entity, ignoreNull) : create(entity);
        }
    }

    default int createOrUpdate(@NonNull T entity) {
        return createOrUpdate(entity, true);
    }

    @UpdateProvider(type = SqlProvider.class, method = SqlProvider.update)
    int update(@NotNull @Param(SqlProvider.CRUD) Update<T, ID> update);

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
    int delete(@NotNull @Param(SqlProvider.CRUD) Delete<T, ID> d);

    default int delete(@NonNull ID id) {
        var d = d();
        d.sqlParamsSize = 1;
        d.addNode(new SqlCond(Tables.getIdName(this), Op.EQ, id));
        return delete(d);
    }

    default int delete(@Nullable Collection<ID> ids) {
        if (CollUtil.isEmpty(ids)) {
            return -1;
        } else {
            var d = d();
            d.sqlParamsSize = 1;
            d.addNode(new SqlCond(Tables.getIdName(this), Op.IN, ids));
            return delete(d);
        }
    }
}