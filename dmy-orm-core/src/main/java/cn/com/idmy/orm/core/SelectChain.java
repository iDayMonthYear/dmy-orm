package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.Node.Distinct;
import cn.com.idmy.orm.core.Node.GroupBy;
import cn.com.idmy.orm.core.Node.OrderBy;
import cn.com.idmy.orm.core.Node.SelectColumn;
import cn.com.idmy.orm.mybatis.MybatisDao;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.text.StrUtil;

import java.util.List;

@Slf4j
@Getter
@Accessors(fluent = true, chain = false)
public class SelectChain<T> extends LambdaWhere<T, SelectChain<T>> {
    protected boolean hasSelectColumn = false;

    protected SelectChain(Class<T> entityClass) {
        super(entityClass);
    }

    public static <T> SelectChain<T> of(MybatisDao<T, ?> dao) {
        return new SelectChain<>(dao.entityClass());
    }

    public SelectChain<T> distinct() {
        hasSelectColumn = true;
        return addNode(new Distinct());
    }

    public SelectChain<T> distinct(ColumnGetter<T, ?> col) {
        hasSelectColumn = true;
        return addNode(new Distinct(col));
    }

    public SelectChain<T> select(SqlFnExpr<T> expr) {
        hasSelectColumn = true;
        return addNode(new SelectColumn(expr));
    }

    public SelectChain<T> select(SqlFnExpr<T> expr, ColumnGetter<T, ?> alias) {
        hasSelectColumn = true;
        return addNode(new SelectColumn(expr, alias));
    }

    @SafeVarargs
    public final SelectChain<T> select(ColumnGetter<T, ?>... cols) {
        if (cols != null) {
            hasSelectColumn = true;
            for (ColumnGetter<T, ?> col : cols) {
                addNode(new SelectColumn(col));
            }
        }
        return this;
    }

    public SelectChain<T> groupBy(ColumnGetter<T, ?> col) {
        return addNode(new GroupBy(col));
    }

    @SafeVarargs
    public final SelectChain<T> groupBy(ColumnGetter<T, ?> col, ColumnGetter<T, ?>... cols) {
        addNode(new GroupBy(col));
        for (ColumnGetter<T, ?> c : cols) {
            addNode(new GroupBy(c));
        }
        return this;
    }

    public SelectChain<T> orderBy(ColumnGetter<T, ?> col) {
        return addNode(new OrderBy(col, false));
    }

    public SelectChain<T> orderBy(ColumnGetter<T, ?> col, boolean desc) {
        return addNode(new OrderBy(col, desc));
    }

    public SelectChain<T> orderBy(ColumnGetter<T, ?> col1, boolean desc1, ColumnGetter<T, ?> col2, boolean desc2) {
        return addNode(new OrderBy(col1, desc1)).addNode(new OrderBy(col2, desc2));
    }

    public SelectChain<T> orderBy(ColumnGetter<T, ?> col1, boolean desc1, ColumnGetter<T, ?> col2, boolean desc2, ColumnGetter<T, ?> col3, boolean desc3) {
        return addNode(new OrderBy(col1, desc1)).addNode(new OrderBy(col2, desc2)).addNode(new OrderBy(col3, desc3));
    }

    public SelectChain<T> orderBy(String[] orders) {
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
