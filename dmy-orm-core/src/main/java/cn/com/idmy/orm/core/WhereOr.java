package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class WhereOr<T> extends Where<T, WhereOr<T>> {
    protected WhereOr(@NotNull Class<T> entityType) {
        super(entityType);
    }

    @Override
    public @NotNull WhereOr<T> or(@NotNull Consumer<WhereOr<T>> consumer) {
        throw new UnsupportedOperationException("不支持");
    }

    @Override
    public @NotNull Pair<String, List<Object>> sql() {
        throw new UnsupportedOperationException("不支持");
    }
}