package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.SqlNode.*;
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
    boolean hasSelectColumn;
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
        return addNode(new SqlDistinct());
    }

    public Selects<T> distinct(ColumnGetter<T, ?> col) {
        hasSelectColumn = true;
        return addNode(new SqlDistinct(col));
    }

    void clearSelectColumns() {
        nodes = nodes.stream().filter(node -> node.type != SqlNodeType.SELECT_COLUMN && node.type != SqlNodeType.DISTINCT).collect(Collectors.toList());
    }

    public Selects<T> select(SqlFnExpr<T> expr) {
        hasSelectColumn = true;
        return addNode(new SqlSelectColumn(expr));
    }

    public Selects<T> select(SqlFnExpr<T> expr, ColumnGetter<T, ?> alias) {
        hasSelectColumn = true;
        return addNode(new SqlSelectColumn(expr, alias));
    }

    @SafeVarargs
    public final Selects<T> select(ColumnGetter<T, ?>... cols) {
        if (ArrayUtil.isNotEmpty(cols)) {
            hasSelectColumn = true;
            for (ColumnGetter<T, ?> col : cols) {
                addNode(new SqlSelectColumn(col));
            }
        }
        return this;
    }

    public Selects<T> groupBy(ColumnGetter<T, ?> col) {
        return addNode(new SqlGroupBy(col));
    }

    @SafeVarargs
    public final Selects<T> groupBy(ColumnGetter<T, ?> col, ColumnGetter<T, ?>... cols) {
        addNode(new SqlGroupBy(col));
        for (ColumnGetter<T, ?> c : cols) {
            addNode(new SqlGroupBy(c));
        }
        return this;
    }

    public Selects<T> orderBy(ColumnGetter<T, ?> col) {
        return addNode(new SqlOrderBy(col, false));
    }

    public Selects<T> orderBy(ColumnGetter<T, ?> col, boolean desc) {
        return addNode(new SqlOrderBy(col, desc));
    }

    public Selects<T> orderBy(ColumnGetter<T, ?> col1, boolean desc1, ColumnGetter<T, ?> col2, boolean desc2) {
        return addNode(new SqlOrderBy(col1, desc1)).addNode(new SqlOrderBy(col2, desc2));
    }

    public Selects<T> orderBy(ColumnGetter<T, ?> col1, boolean desc1, ColumnGetter<T, ?> col2, boolean desc2, ColumnGetter<T, ?> col3, boolean desc3) {
        return addNode(new SqlOrderBy(col1, desc1)).addNode(new SqlOrderBy(col2, desc2)).addNode(new SqlOrderBy(col3, desc3));
    }

    public Selects<T> orderBy(String[] orders) {
        Assert.isTrue(orders.length % 2 == 0, "排序列名不成对，必须为：['name', 'asc', 'gender', 'desc']");
        for (int i = 0; i < orders.length; i = i + 2) {
            addNode(new SqlOrderBy(orders[i], StrUtil.equalsIgnoreCase(orders[i + 1], "desc")));
        }
        return this;
    }

    @Override
    public Pair<String, List<Object>> sql() {
        return new SelectSqlGenerator(this).generate();
    }
}
