package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.SqlNode.SqlOr;
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
        addNode(SqlOr.OR);
        addNode(new SqlNode(Type.LEFT_BRACKET));
        consumer.accept(subWhere);
        nodes.addAll(subWhere.nodes);
        addNode(new SqlNode(Type.RIGHT_BRACKET));
        return this;
    }

    public @NotNull WhereLogic<T> or() {
        // 检查前面的节点，如果最后一个节点是条件(COND)，自动添加括号
        // 这会将当前条件和下一个条件(即将通过or连接的条件)都包在括号里
        if (!nodes.isEmpty()) {
            // 找到最近的条件节点和相应的位置
            int lastCondIndex = -1;
            for (int i = nodes.size() - 1; i >= 0; i--) {
                if (nodes.get(i) instanceof SqlNode.SqlCond) {
                    lastCondIndex = i;
                    break;
                }
            }
            
            if (lastCondIndex >= 0) {
                // 在最近的条件前插入左括号
                nodes.add(lastCondIndex, new SqlNode(Type.LEFT_BRACKET));
                // 添加右括号（会被放在OR前）
                addNode(new SqlNode(Type.RIGHT_BRACKET));
            }
        }
        
        // 添加OR运算符
        addNode(SqlOr.OR);
        // 添加左括号，为下一个条件准备
        addNode(new SqlNode(Type.LEFT_BRACKET));
        return this;
    }

    /**
     * 特殊处理：如果前面是OR操作符后跟LEFT_BRACKET，
     * 而最后一个节点是条件节点，则添加闭合括号
     * 在任何条件生成完成后调用此方法
     */
    private void closeOrBracketIfNeeded() {
        if (nodes.size() >= 3) {
            int lastIndex = nodes.size() - 1;
            // 检查前面是否有"OR ("模式
            if (nodes.get(lastIndex - 2) instanceof SqlOr &&
                nodes.get(lastIndex - 1).type == Type.LEFT_BRACKET &&
                nodes.get(lastIndex) instanceof SqlNode.SqlCond) {
                // 添加闭合括号
                addNode(new SqlNode(Type.RIGHT_BRACKET));
            }
        }
    }
    
    @Override
    public @NotNull WhereLogic<T> addNode(@NotNull SqlNode node) {
        super.addNode(node);
        // 如果添加的是条件节点，检查是否需要闭合前面的"OR ("
        if (node instanceof SqlNode.SqlCond) {
            closeOrBracketIfNeeded();
        }
        return this;
    }

    @Override
    public @NotNull WhereLogic<T> and(@NotNull Consumer<WhereLogic<T>> consumer) {
        var subWhere = new WhereLogic<>(entityType);
        subWhere.nullable = false;
        addNode(new SqlNode(Type.LEFT_BRACKET));
        consumer.accept(subWhere);
        nodes.addAll(subWhere.nodes);
        addNode(new SqlNode(Type.RIGHT_BRACKET));
        return this;
    }

    @Override
    public @NotNull Pair<String, List<Object>> sql() {
        throw new UnsupportedOperationException("不支持");
    }
}