package cn.com.idmy.orm.core;

import cn.com.idmy.orm.core.SqlNode.SqlColumn;
import cn.com.idmy.orm.core.SqlNode.SqlOr;
import cn.com.idmy.orm.util.OrmUtil;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.lang.tuple.Pair;
import org.dromara.hutool.core.text.StrUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Accessors(fluent = true, chain = true)
abstract class Crud<T, CRUD extends Crud<T, CRUD>> {
    @SuppressWarnings({"unchecked"})
    protected final @NotNull CRUD crud = (CRUD) this;
    protected @NotNull List<SqlNode> nodes = new ArrayList<>();
    @Getter
    protected @NotNull Class<T> entityType;
    protected int sqlParamsSize; // sql 参数数量，优化手段减少数组分配大小
    protected boolean nullable;
    protected boolean hasCond;
    protected boolean force;

    protected Crud(@NotNull Class<T> entityType) {
        this.entityType = entityType;
    }

    public @NotNull CRUD force() {
        this.force = true;
        return crud;
    }

    public abstract @NotNull Pair<String, List<Object>> sql();

    @Override
    public String toString() {
        try {
            return sql().getLeft();
        } catch (Exception e) {
            return StrUtil.contains(e.getMessage(), "force") ? "" : e.getMessage();
        }
    }

    protected @NotNull CRUD addNode(@NotNull SqlNode node) {
        nodes.add(node);
        return crud;
    }

    protected boolean hasColumn(@NotNull String column, @NotNull SqlNode.Type type) {
        return OrmUtil.hasColumn(nodes, column, type);
    }

    protected @NotNull List<SqlNode> columns(@NotNull String column) {
        return nodes.stream().filter(n -> n instanceof SqlColumn col && Objects.equals(col.column(), column)).toList();
    }

    public @NotNull CRUD or() {
        return addNode(SqlOr.OR);
    }
}