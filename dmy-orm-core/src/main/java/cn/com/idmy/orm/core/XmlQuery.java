package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Page;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Slf4j
@Accessors(fluent = true, chain = true)
public class XmlQuery<T> extends Query<T> {
    private transient @Nullable XmlQueryGenerator generator;
    protected @Nullable Boolean hasTotal;
    @Getter
    protected long total;
    protected Object params;
    protected @Nullable Page<T> page;

    protected XmlQuery(@NotNull Class<T> entityType, boolean nullable) {
        super(entityType, nullable);
        force = true;
    }

    @SuppressWarnings({"unchecked"})
    protected XmlQuery(@NotNull Page<T> page, boolean nullable) {
        this((Class<T>) page.params().getClass(), nullable);
        this.page = page;
        params = page.params();
        offset = page.offset();
        limit = page.pageSize();
        hasTotal = page.hasTotal();
        orderBy(page.sorts());
    }

    private @NotNull XmlQueryGenerator generator() {
        if (generator == null) {
            generator = XmlQueryGenerator.of(this);
        }
        return generator;
    }

    public @Nullable String getWhere() {
        return generator().getWhereString();
    }

    public @Nullable String getOrderBy() {
        return generator().getOrderByString();
    }

    public @Nullable String getGroupBy() {
        return generator().getGroupByString();
    }

    public @NotNull List<Object> getValues() {
        return generator().values;
    }

    public @NotNull Object getParams() {
        return params;
    }

    @Override
    public @NotNull XmlQuery<T> addNode(@NotNull SqlNode node) {
        super.addNode(node);
        this.generator = null;
        return this;
    }
}