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
public class Update<T, ID> extends Where<T, ID, Update<T, ID>> {

    protected Update(@NotNull OrmDao<T, ID> dao, boolean nullable) {
        super(dao);
        this.nullable = nullable;
    }

    @NotNull
    protected static <T, ID> Update<T, ID> of(@NotNull OrmDao<T, ID> dao, boolean nullable) {
        return new Update<>(dao, nullable);
    }

    @NotNull
    public Update<T, ID> set(@NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return addNode(new SqlSet(entityType, field, val));
    }

    @NotNull
    public Update<T, ID> set(@NotNull FieldGetter<T, ?> field, @Nullable SqlOpExpr expr) {
        return addNode(new SqlSet(entityType, field, expr));
    }

    @NotNull
    public Update<T, ID> set(boolean if0, @NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return if0 ? set(field, val) : crud;
    }

    @NotNull
    public Update<T, ID> set(boolean if0, @NotNull FieldGetter<T, ?> field, @Nullable SqlOpExpr expr) {
        return if0 ? set(field, expr) : crud;
    }

    @NotNull
    @Override
    public Pair<String, List<Object>> sql() {
        return new UpdateSqlGenerator(this).gen();
    }
}
