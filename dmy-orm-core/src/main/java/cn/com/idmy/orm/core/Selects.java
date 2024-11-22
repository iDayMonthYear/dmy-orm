package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.Node.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.text.StrUtil;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Accessors(fluent = true, chain = false)
public class Selects<T> extends Where<T, Selects<T>> {
    boolean hasSelectColumn = false;
    @Getter
    @Setter
    protected Integer offset;
    @Getter
    @Setter
    protected Integer limit;

    protected Selects(Class<T> entityClass) {
        super(entityClass);
    }

    public static <T, ID> Selects<T> of(MybatisDao<T, ID> dao) {
        return new Selects<>(dao.entityClass());
    }

    public Selects<T> distinct() {
        hasSelectColumn = true;
        return addNode(new Distinct());
    }

    public Selects<T> distinct(ColumnGetter<T, ?> col) {
        hasSelectColumn = true;
        return addNode(new Distinct(col));
    }

    void clearSelectColumns() {
        nodes = nodes.stream().filter(node -> node.type != Type.SELECT_COLUMN && node.type != Type.DISTINCT).collect(Collectors.toList());
    }

    public Selects<T> select(SqlFnExpr<T> expr) {
        hasSelectColumn = true;
        return addNode(new SelectColumn(expr));
    }

    public Selects<T> select(SqlFnExpr<T> expr, ColumnGetter<T, ?> alias) {
        hasSelectColumn = true;
        return addNode(new SelectColumn(expr, alias));
    }

    @SafeVarargs
    public final Selects<T> select(ColumnGetter<T, ?>... cols) {
        if (ArrayUtil.isNotEmpty(cols)) {
            hasSelectColumn = true;
            for (ColumnGetter<T, ?> col : cols) {
                addNode(new SelectColumn(col));
            }
        }
        return this;
    }

    public Selects<T> groupBy(ColumnGetter<T, ?> col) {
        return addNode(new GroupBy(col));
    }

    @SafeVarargs
    public final Selects<T> groupBy(ColumnGetter<T, ?> col, ColumnGetter<T, ?>... cols) {
        addNode(new GroupBy(col));
        for (ColumnGetter<T, ?> c : cols) {
            addNode(new GroupBy(c));
        }
        return this;
    }

    public Selects<T> orderBy(ColumnGetter<T, ?> col) {
        return addNode(new OrderBy(col, false));
    }

    public Selects<T> orderBy(ColumnGetter<T, ?> col, boolean desc) {
        return addNode(new OrderBy(col, desc));
    }

    public Selects<T> orderBy(ColumnGetter<T, ?> col1, boolean desc1, ColumnGetter<T, ?> col2, boolean desc2) {
        return addNode(new OrderBy(col1, desc1)).addNode(new OrderBy(col2, desc2));
    }

    public Selects<T> orderBy(ColumnGetter<T, ?> col1, boolean desc1, ColumnGetter<T, ?> col2, boolean desc2, ColumnGetter<T, ?> col3, boolean desc3) {
        return addNode(new OrderBy(col1, desc1)).addNode(new OrderBy(col2, desc2)).addNode(new OrderBy(col3, desc3));
    }

    public Selects<T> orderBy(String[] orders) {
        Assert.isTrue(orders.length % 2 == 0, "排序列名不成对，必须为：['name', 'asc', 'gender', 'desc']");
        for (int i = 0; i < orders.length; i = i + 2) {
            addNode(new OrderBy(orders[i], StrUtil.equalsIgnoreCase(orders[i + 1], "desc")));
        }
        return this;
    }

    @Override
    public Pair<String, List<Object>> sql() {
        return SelectSqlGenerator.gen(this);
    }
}
