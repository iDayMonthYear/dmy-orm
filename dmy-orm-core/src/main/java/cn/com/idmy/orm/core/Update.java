package cn.com.idmy.orm.core;

import cn.com.idmy.base.FieldGetter;
import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.SqlNode.SqlSet;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


@Slf4j
@Accessors(fluent = true, chain = false)
public class Update<T> extends Where<T, Update<T>> {
    protected Update(@NotNull Class<T> entityType, boolean nullable) {
        super(entityType);
        this.nullable = nullable;
    }

    @NotNull
    public Update<T> set(@NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return addNode(new SqlSet(entityType, field, val));
    }

    @NotNull
    public Update<T> set(@NotNull FieldGetter<T, ?> field, @Nullable SqlOpExpr expr) {
        return addNode(new SqlSet(entityType, field, expr));
    }

    @NotNull
    public Update<T> set(boolean if0, @NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return if0 ? set(field, val) : crud;
    }

    @NotNull
    public Update<T> set(boolean if0, @NotNull FieldGetter<T, ?> field, @Nullable SqlOpExpr expr) {
        return if0 ? set(field, expr) : crud;
    }

    @NotNull
    @Override
    public Pair<String, List<Object>> sql() {
        return new UpdateSqlGenerator(this).generate();
    }
}
