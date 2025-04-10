package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.SqlNode.Type;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class WhereLogic<T> extends Where<T, WhereLogic<T>> {
    protected WhereLogic(@NotNull Class<T> entityType) {
        super(entityType);
    }

    @Override
    public @NotNull WhereLogic<T> or(@NotNull Consumer<WhereLogic<T>> consumer) {
        var subWhere = new WhereLogic<>(entityType);
        subWhere.nullable = false;
        consumer.accept(subWhere);
        addNode(new SqlNode(Type.OR));
        addNode(new SqlNode(Type.LEFT_BRACKET));
        nodes.addAll(subWhere.nodes);
        addNode(new SqlNode(Type.RIGHT_BRACKET));
        return this;
    }

    public @NotNull WhereLogic<T> or() {
        addNode(new SqlNode(Type.OR));
        return this;
    }

    @Override
    public @NotNull WhereLogic<T> and(@NotNull Consumer<WhereLogic<T>> consumer) {
        var subWhere = new WhereLogic<>(entityType);
        subWhere.nullable = false;
        consumer.accept(subWhere);
        addNode(new SqlNode(Type.AND));
        addNode(new SqlNode(Type.LEFT_BRACKET));
        nodes.addAll(subWhere.nodes);
        addNode(new SqlNode(Type.RIGHT_BRACKET));
        return this;
    }

    @Override
    public @NotNull Pair<String, List<Object>> sql() {
        throw new UnsupportedOperationException("不支持");
    }
}