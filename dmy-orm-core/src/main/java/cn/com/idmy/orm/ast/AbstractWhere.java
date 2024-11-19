package cn.com.idmy.orm.ast;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.ast.Node.Or;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Accessors(fluent = true, chain = false)
public abstract class AbstractWhere<T, WHERE extends AbstractWhere<T, WHERE>> {
    protected final List<Node> nodes = new ArrayList<>();
    protected final WHERE typedThis = (WHERE) this;
    protected Class<T> entityClass;

    protected AbstractWhere(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public abstract Pair<String, List<Object>> sql();

    @Override
    public String toString() {
        try {
            return sql().left;
        } catch (Exception e) {
            log.warn("SQL生成失败：{}", e.getMessage());
            return null;
        }
    }

    protected WHERE addNode(Node ast) {
        nodes.add(ast);
        return typedThis;
    }

    public WHERE or() {
        return addNode(new Or());
    }
}