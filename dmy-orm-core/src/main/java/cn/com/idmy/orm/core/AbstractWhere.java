package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.Node.Or;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Accessors(fluent = true, chain = true)
public abstract class AbstractWhere<T, WHERE extends AbstractWhere<T, WHERE>> {
    @Getter(value = AccessLevel.PROTECTED)
    final List<Node> nodes = new ArrayList<>();

    @SuppressWarnings({"unchecked"})
    protected final WHERE $this = (WHERE) this;

    @Getter(value = AccessLevel.PROTECTED)
    protected Class<T> entityClass;

    @Setter(value = AccessLevel.PROTECTED)
    @Getter(value = AccessLevel.PROTECTED)
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

    WHERE addNode(@Nullable Node node) {
        nodes.add(node);
        return $this;
    }

    public WHERE or() {
        return addNode(new Or());
    }
}