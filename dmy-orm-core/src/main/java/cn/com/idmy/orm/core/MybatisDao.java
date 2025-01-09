package cn.com.idmy.orm.core;

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

public interface MybatisDao<T, ID> {
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
    default Query<T> q() {
        return Query.of(this);
    }

    @NotNull
    default Update<T> u() {
        return Update.of(this);
    }

    @NotNull
    default Delete<T> d() {
        return Delete.of(this);
    }

    @SelectProvider(type = MybatisSqlProvider.class, method = MybatisSqlProvider.count)
    long count(@NotNull @Param(MybatisSqlProvider.CRUD) Query<T> q);

    default boolean exists(@NotNull Query<T> q) {
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

    default void exists(@NotNull Query<T> q, @NotNull String msg, @NotNull Object... params) {
        if (!exists(q)) {
            throw new IllegalStateException(String.format(msg, params));
        }
    }

    default boolean notExists(@NotNull Query<T> q) {
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

    default void notExists(@NotNull Query<T> q, @NotNull String msg, @NotNull Object... params) {
        if (!notExists(q)) {
            throw new IllegalStateException(String.format(msg, params));
        }
    }

    @Nullable
    @SelectProvider(type = MybatisSqlProvider.class, method = MybatisSqlProvider.find0)
    List<T> find0(@NotNull @Param(MybatisSqlProvider.CRUD) Query<T> q);

    @NotNull
    default List<T> find(@NotNull Query<T> q) {
        List<T> ts = find0(q);
        return ts == null ? new ArrayList<T>(0) : ts;
    }

    @NotNull
    default List<T> all() {
        return find(q());
    }

    @NotNull
    default List<T> find(@Nullable Collection<ID> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        } else {
            var q = q();
            q.sqlParamsSize = 1;
            q.addNode(new SqlCond(Tables.getIdName(this), Op.IN, ids));
            return find(q);
        }
    }

    @NotNull
    default List<T> find(@NotNull Collection<ID> ids, @NotNull String msg, @NotNull Object... params) {
        return Assert.notEmpty(find(ids), msg, params);
    }

    @NotNull
    default <R> List<R> find(@NotNull FieldGetter<T, R> field, @Nullable Collection<ID> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        } else {
            var q = q().select(field);
            q.sqlParamsSize = 1;
            q.addNode(new SqlCond(Tables.getIdName(this), Op.IN, ids));
            return find(q).stream().map(field::get).toList();
        }
    }

    @NotNull
    default <R> List<R> find(@NotNull FieldGetter<T, R> field, @NotNull Query<T> q) {
        MybatisSqlProvider.clearSelectColumns(q);
        var ts = find(q.select(field));
        return CollStreamUtil.toList(ts, field::get);
    }

    @NotNull
    default List<T> find(@NotNull Query<T> q, @NotNull FieldGetter<T, ?> field, FieldGetter<T, ?>... fields) {
        q.select(field);
        q.select(fields);
        return find(q);
    }

    @Nullable
    @SelectProvider(type = MybatisSqlProvider.class, method = MybatisSqlProvider.getNullable)
    T getNullable(@NotNull @Param(MybatisSqlProvider.CRUD) Query<T> q);

    @NotNull
    default T get(@NotNull Query<T> q, @NotNull String msg, @NotNull Object... params) {
        return Assert.notNull(getNullable(q), msg, params);
    }

    @NotNull
    default T get(@NotNull Query<T> q) {
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
    default <R> R getNullable(@NotNull FieldGetter<T, R> field, @NotNull Query<T> q) {
        MybatisSqlProvider.clearSelectColumns(q);
        q.limit = 1;
        q.select(field);
        T t = getNullable(q);
        return t == null ? null : field.get(t);
    }

    @NotNull
    default <R> R get(@NotNull FieldGetter<T, R> field, @NotNull Query<T> q, @NotNull String msg, @NotNull Object... params) {
        return Assert.notNull(getNullable(field, q), msg, params);
    }

    @NotNull
    default <R> R get(@NotNull FieldGetter<T, R> field, @NotNull Query<T> q) {
        R r = getNullable(field, q);
        if (r == null) {
            var col = Tables.getColum(entityType(), field);
            assert col != null;
            throw new OrmException("根据主键「查询条件」找不到「{}」", Optional.ofNullable(col.title()).orElse(col.name()));
        } else {
            return r;
        }
    }

    @Nullable
    default T getNullable(@NotNull Query<T> q, @NotNull FieldGetter<T, ?> field, FieldGetter<T, ?>... fields) {
        MybatisSqlProvider.clearSelectColumns(q);
        q.select(field);
        q.select(fields);
        return getNullable(q);
    }

    @NotNull
    default Map<ID, T> map(@Nullable ID... ids) {
        return ArrayUtil.isEmpty(ids) ? Collections.emptyMap() : MybatisSqlProvider.map(this, ids);
    }

    @NotNull
    default Map<ID, T> map(@Nullable Collection<ID> ids) {
        return CollUtil.isEmpty(ids) ? Collections.emptyMap() : MybatisSqlProvider.map(this, ids);
    }

    @NotNull
    default <R> Map<R, T> map(@NotNull FieldGetter<T, R> field, @NotNull Query<T> q) {
        return CollStreamUtil.toIdentityMap(find(q), field::get);
    }

    @NotNull
    default <IN> Page<T> page(@NonNull Page<IN> page, @NotNull Query<T> q) {
        return MybatisSqlProvider.page(this, page, q);
    }

    @Nullable
    default <R extends Number> R sqlFn(@NotNull SqlFnName name, @NotNull FieldGetter<T, R> field, @NotNull Query<T> q) {
        if (name == SqlFnName.IF_NULL) {
            throw new OrmException("不支持ifnull");
        } else {
            MybatisSqlProvider.clearSelectColumns(q);
            q.limit = 1;
            T t = getNullable(q.select(() -> new SqlFn<>(name, field)));
            return t == null ? null : field.get(t);
        }
    }

    @Nullable
    default <R extends Number> R abs(@NotNull FieldGetter<T, R> field, @NotNull Query<T> q) {
        return sqlFn(SqlFnName.ABS, field, q);
    }

    @Nullable
    default <R extends Number> R avg(@NotNull FieldGetter<T, R> field, @NotNull Query<T> q) {
        return sqlFn(SqlFnName.AVG, field, q);
    }

    @Nullable
    default <R extends Number> R max(@NotNull FieldGetter<T, R> field, @NotNull Query<T> q) {
        return sqlFn(SqlFnName.MAX, field, q);
    }

    @Nullable
    default <R extends Number> R min(@NotNull FieldGetter<T, R> field, @NotNull Query<T> q) {
        return sqlFn(SqlFnName.MIN, field, q);
    }

    @NotNull
    default <R extends Number> R sum(@NotNull FieldGetter<T, R> field, @NotNull Query<T> q) {
        R result = sqlFn(SqlFnName.SUM, field, q);
        if (result == null) {
            var fieldType = ClassUtil.getTypeArgument(field.getClass());
            @SuppressWarnings("unchecked") R zero = (R) ConvertUtil.convert(fieldType, 0);
            return zero;
        } else {
            return result;
        }
    }

    @InsertProvider(type = MybatisSqlProvider.class, method = MybatisSqlProvider.create)
    int create(@NonNull @Param(MybatisSqlProvider.ENTITY) T entity);

    @InsertProvider(type = MybatisSqlProvider.class, method = MybatisSqlProvider.creates)
    int creates(@NonNull @Param(MybatisSqlProvider.ENTITIES) Collection<T> entities);

    default int creates(@Nullable Collection<T> entities, int size) {
        return MybatisSqlProvider.creates(this, entities, size);
    }

    default int createOrUpdate(@NonNull T entity, boolean ignoreNull) {
        ID idVal = Tables.getIdValue(entity);
        if (idVal == null) {
            return create(entity);
        } else {
            return exists(idVal) ? MybatisSqlProvider.update(this, entity, ignoreNull) : create(entity);
        }
    }

    default int createOrUpdate(@NonNull T entity) {
        return createOrUpdate(entity, true);
    }

    @UpdateProvider(type = MybatisSqlProvider.class, method = MybatisSqlProvider.update)
    int update(@NotNull @Param(MybatisSqlProvider.CRUD) Update<T> update);

    default int update(@NonNull T entity) {
        return update(entity, true);
    }

    default int update(@NonNull T entity, boolean ignoreNull) {
        return MybatisSqlProvider.update(this, entity, ignoreNull);
    }

    default int[] update(@Nullable Collection<T> entities, int size, boolean ignoreNull) {
        if (entities == null) {
            return new int[]{0};
        } else {
            return MybatisSqlProvider.update(this, entities, size, ignoreNull);
        }
    }

    @UpdateProvider(type = MybatisSqlProvider.class, method = MybatisSqlProvider.updateBySql)
    int updateBySql(@NonNull @Param(MybatisSqlProvider.CRUD) String sql, @NonNull @Param(MybatisSqlProvider.SQL_PARAMS) List<Object> params);

    @DeleteProvider(type = MybatisSqlProvider.class, method = MybatisSqlProvider.delete)
    int delete(@NotNull @Param(MybatisSqlProvider.CRUD) Delete<T> d);

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