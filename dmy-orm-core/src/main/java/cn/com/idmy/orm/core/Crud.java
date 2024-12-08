package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.SqlNode.SqlColumn;
import cn.com.idmy.orm.core.SqlNode.SqlNodeType;
import cn.com.idmy.orm.core.SqlNode.SqlOr;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Accessors(fluent = true, chain = true)
abstract class Crud<T, CRUD extends Crud<T, CRUD>> {
    @Getter(value = AccessLevel.PROTECTED)
    protected List<SqlNode> nodes = new ArrayList<>();

    @SuppressWarnings({"unchecked"})
    protected final CRUD $this = (CRUD) this;

    @Getter(value = AccessLevel.PROTECTED)
    protected Class<T> entityClass;

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

    protected CRUD addNode(SqlNode node) {
        nodes.add(node);
        return $this;
    }

    protected boolean hasColumn(String column, SqlNodeType type) {
        return nodes.stream().anyMatch(n -> {
            if (n instanceof SqlColumn col) {
                return Objects.equals(col.column(), column) && n.type() == type;
            } else {
                return false;
            }
        });
    }

    protected List<SqlNode> columns(String column) {
        return nodes.stream().filter(n -> n instanceof SqlColumn col && Objects.equals(col.column(), column)).toList();
    }

    public CRUD or() {
        return addNode(new SqlOr());
    }
}