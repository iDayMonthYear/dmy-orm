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
    @Nullable
    private transient XmlQueryGenerator generator;
    @Nullable
    protected Boolean hasTotal;
    @Getter
    protected long total;
    protected Object params;
    @Nullable
    protected Page<T> page;

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
        if (page.pageSize() == 1 && page.pageNo() == 1 && hasTotal == null) {
            hasTotal = false;
        }
        orderBy(page.sorts());
    }

    @NotNull
    private XmlQueryGenerator generator() {
        if (generator == null) {
            generator = XmlQueryGenerator.of(this);
        }
        return generator;
    }

    @Nullable
    public String getWhere() {
        return generator().getWhereString();
    }

    @Nullable
    public String getOrderBy() {
        return generator().getOrderByString();
    }

    @Nullable
    public String getGroupBy() {
        return generator().getGroupByString();
    }

    @NotNull
    public List<Object> getValues() {
        return generator().values;
    }

    @NotNull
    public Object getParams() {
        return params;
    }

    @Override
    @NotNull
    public XmlQuery<T> addNode(@NotNull SqlNode node) {
        super.addNode(node);
        this.generator = null;
        return this;
    }
}