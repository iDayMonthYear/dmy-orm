package cn.com.idmy.orm.core.ast;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.OrmDao;
import cn.com.idmy.orm.core.ast.Node.Distinct;
import cn.com.idmy.orm.core.ast.Node.GroupBy;
import cn.com.idmy.orm.core.ast.Node.OrderBy;
import cn.com.idmy.orm.core.ast.Node.SelectField;
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

    private SelectChain(Class<T> table) {
        super(table);
    }

    public static <T> SelectChain<T> of(OrmDao<T> dao) {
        return new SelectChain<>(dao.entityType());
    }

    public SelectChain<T> distinct() {
        return addNode(new Distinct());
    }

    public SelectChain<T> distinct(FieldGetter<T, ?> field) {
        return addNode(new Distinct(field));
    }

    public SelectChain<T> select() {
        return this;
    }

    public SelectChain<T> select(SqlFnExpr<T> expr) {
        return addNode(new SelectField(expr));
    }

    public SelectChain<T> select(SqlFnExpr<T> expr, FieldGetter<T, ?> alias) {
        return addNode(new SelectField(expr, alias));
    }

    @SafeVarargs
    public final SelectChain<T> select(FieldGetter<T, ?> field, FieldGetter<T, ?>... fields) {
        addNode(new SelectField(field));
        for (FieldGetter<T, ?> f : fields) {
            addNode(new SelectField(f));
        }
        return this;
    }

    public SelectChain<T> groupBy(FieldGetter<T, ?> field) {
        return addNode(new GroupBy(field));
    }

    @SafeVarargs
    public final SelectChain<T> groupBy(FieldGetter<T, ?> field, FieldGetter<T, ?>... fields) {
        addNode(new GroupBy(field));
        for (FieldGetter<T, ?> f : fields) {
            addNode(new GroupBy(f));
        }
        return this;
    }

    public SelectChain<T> orderBy(FieldGetter<T, ?> field) {
        return addNode(new OrderBy(field, false));
    }

    public SelectChain<T> orderBy(FieldGetter<T, ?> field, boolean desc) {
        return addNode(new OrderBy(field, desc));
    }

    public SelectChain<T> orderBy(FieldGetter<T, ?> field1, boolean desc1, FieldGetter<T, ?> field2, boolean desc2) {
        return addNode(new OrderBy(field1, desc1)).addNode(new OrderBy(field2, desc2));
    }

    public SelectChain<T> orderBy(FieldGetter<T, ?> field1, boolean desc1, FieldGetter<T, ?> field2, boolean desc2, FieldGetter<T, ?> field3, boolean desc3) {
        return addNode(new OrderBy(field1, desc1)).addNode(new OrderBy(field2, desc2)).addNode(new OrderBy(field3, desc3));
    }

    public SelectChain<T> orderBy(String[] orders) {
        Assert.isTrue(orders.length % 2 == 0, "排序字段不成对，必须为：['name', 'asc', 'gender', 'desc']");
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
