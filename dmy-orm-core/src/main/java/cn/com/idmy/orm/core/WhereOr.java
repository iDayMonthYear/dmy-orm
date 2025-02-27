package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class WhereOr<T, ID> extends Where<T, ID, WhereOr<T, ID>> {
    protected WhereOr(@NotNull Class<T> entityType) {
        super(entityType);
    }

    @Override
    public @NotNull WhereOr<T, ID> or(@NotNull Consumer<WhereOr<T, ID>> consumer) {
        throw new UnsupportedOperationException("不支持");
    }

    @NotNull
    @Override
    public Pair<String, List<Object>> sql() {
        throw new UnsupportedOperationException("不支持");
    }
}