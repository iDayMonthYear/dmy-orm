package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.Node.Column;
import cn.com.idmy.orm.core.Node.Or;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Accessors(fluent = true, chain = true)
public abstract class Crud<T, CRUD extends Crud<T, CRUD>> {
    @Getter(value = AccessLevel.PROTECTED)
    protected List<Node> nodes = new ArrayList<>();

    @SuppressWarnings({"unchecked"})
    protected final CRUD $this = (CRUD) this;

    @Getter(value = AccessLevel.PROTECTED)
    protected Class<T> entityClass;

    @Setter(value = AccessLevel.PROTECTED)
    @Getter(value = AccessLevel.PROTECTED)
    protected int sqlParamsSize;

    protected Crud(Class<T> entityClass) {
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

    protected CRUD addNode(Node node) {
        nodes.add(node);
        return $this;
    }

    protected boolean hasColumn(String column, Node.Type type) {
        return nodes.stream().anyMatch(n -> {
            if (n instanceof Column col) {
                return Objects.equals(col.column(), column) && n.type() == type;
            } else {
                return false;
            }
        });
    }

    protected List<Node> columns(String column) {
        return nodes.stream().filter(n -> n instanceof Column col && Objects.equals(col.column(), column)).toList();
    }

    public CRUD or() {
        return addNode(new Or());
    }
}