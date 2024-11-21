package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.Node.Or;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Accessors(fluent = true, chain = true)
public abstract class AbstractWhere<T, WHERE extends AbstractWhere<T, WHERE>> {
    protected final List<Node> nodes = new ArrayList<>();
    @SuppressWarnings({"unchecked"})
    protected final WHERE $this = (WHERE) this;
    protected Class<T> entityClass;
    @Setter
    protected int sqlParamsSize;

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
            return "异常";
        }
    }

    protected WHERE addNode(Node node) {
        nodes.add(node);
        return $this;
    }

    public WHERE or() {
        return addNode(new Or());
    }
}