package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.Node.Or;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Accessors(fluent = true, chain = true)
public abstract class Rud<T, RUD extends Rud<T, RUD>> {
    @Getter(value = AccessLevel.PROTECTED)
    List<Node> nodes = new ArrayList<>();

    @SuppressWarnings({"unchecked"})
    protected final RUD $this = (RUD) this;

    @Getter(value = AccessLevel.PROTECTED)
    protected Class<T> entityClass;

    @Setter(value = AccessLevel.PROTECTED)
    @Getter(value = AccessLevel.PROTECTED)
    protected int sqlParamsSize;

    protected Rud(Class<T> entityClass) {
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

    RUD addNode(Node node) {
        nodes.add(node);
        return $this;
    }

    public RUD or() {
        return addNode(new Or());
    }
}