package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.SqlNode.SqlColumn;
import cn.com.idmy.orm.core.SqlNode.SqlOr;
import cn.com.idmy.orm.util.OrmUtil;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Accessors(fluent = true, chain = true)
abstract class Crud<T, ID, CRUD extends Crud<T, ID, CRUD>> {
    @SuppressWarnings({"unchecked"})
    @NotNull
    protected final CRUD crud = (CRUD) this;
    @NotNull
    protected List<SqlNode> nodes = new ArrayList<>();
    @NotNull
    protected Class<T> entityType;
    protected int sqlParamsSize;
    protected boolean nullable;

    protected Crud(@NotNull Class<T> entityType) {
        this.entityType = entityType;
    }


    @NotNull
    public abstract Pair<String, List<Object>> sql();

    @Override
    public String toString() {
        try {
            return sql().left;
        } catch (Exception e) {
            return "异常：" + e.getMessage();
        }
    }

    @NotNull
    protected CRUD addNode(@NotNull SqlNode node) {
        nodes.add(node);
        return crud;
    }

    protected boolean hasColumn(@NotNull String column, @NotNull SqlNode.Type type) {
        return OrmUtil.hasColumn(nodes, column, type);
    }

    @NotNull
    protected List<SqlNode> columns(@NotNull String column) {
        return nodes.stream().filter(n -> n instanceof SqlColumn col && Objects.equals(col.column(), column)).toList();
    }

    @NotNull
    public CRUD or() {
        return addNode(new SqlOr());
    }
}