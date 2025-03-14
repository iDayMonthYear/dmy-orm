package cn.com.idmy.orm.core;

import cn.com.idmy.base.FieldGetter;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.util.OrmUtil;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public interface OrmMultiIdsDao<T, ID> extends OrmDao<T, ID> {
    @Override
    default int delete(@NonNull ID id) {
        var d = d();
        d.sqlParamsSize = 1;
        OrmUtil.multiIdsAddEqNode(id, d);
        return delete(d);
    }

    @Override
    default boolean has(@NonNull ID id) {
        var q = q();
        q.sqlParamsSize = 1;
        OrmUtil.multiIdsAddEqNode(id, q);
        return has(q);
    }

    @Nullable
    default T getNullable(@NonNull ID id) {
        var q = q();
        q.sqlParamsSize = 1;
        OrmUtil.multiIdsAddEqNode(id, q);
        return getNullable(q);
    }

    @Override
    default int update(@NonNull T entity) {
        throw new OrmException("该方法暂时不支持多主键，请使用链式更新");
    }

    @Override
    default int update(@NonNull T entity, boolean ignoreNull) {
        throw new OrmException("该方法暂时不支持多主键，请使用链式更新");
    }

    @Override
    default int[] update(@Nullable Collection<T> entities, int size, boolean ignoreNull) {
        throw new OrmException("该方法暂时不支持多主键，请使用链式更新");
    }

    @Override
    @Nullable
    default <R> R getNullable(@NotNull FieldGetter<T, R> field, @NonNull ID id) {
        throw new OrmException("该方法暂时不支持多主键");
    }

    @Override
    @NotNull
    default <R> R get(@NotNull FieldGetter<T, R> field, @NonNull ID id) {
        throw new OrmException("该方法暂时不支持多主键");
    }

    @Override
    @NotNull
    default <R> R get(@NotNull FieldGetter<T, R> field, @NonNull ID id, @NotNull String msg, @NotNull Object... params) {
        throw new OrmException("该方法暂时不支持多主键");
    }

    @Override
    default @NotNull Map<ID, T> map(@Nullable ID... ids) {
        throw new OrmException("该方法暂时不支持多主键");
    }

    @Override
    default @NotNull Map<ID, T> map(@Nullable Collection<ID> ids) {
        throw new OrmException("该方法暂时不支持多主键");
    }

    @Override
    default int createOrUpdate(@NonNull T entity, boolean ignoreNull) {
        throw new OrmException("该方法暂时不支持多主键");
    }
}