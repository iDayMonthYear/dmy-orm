package cn.com.idmy.orm.core.ast;

import cn.com.idmy.orm.core.OrmDao;
import cn.com.idmy.orm.core.ast.Node.*;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Accessors(fluent = true, chain = false)
public class SelectChain<T> extends StringWhere<T, SelectChain<T>> {

    private SelectChain(Class<T> table) {
        super(table);
    }

    public static <T> SelectChain<T> of(OrmDao<T> dao) {
        return new SelectChain<>(dao.entityType());
    }

    public SelectChain<T> distinct() {
        return addNode(new Distinct());
    }

    public SelectChain<T> distinct(String field) {
        return addNode(new Distinct(new Field(field)));
    }

    public SelectChain<T> distinct(FieldGetter<T, ?> getter) {
        return addNode(new Distinct(new Field(getter)));
    }

    public SelectChain<T> select() {
        return this;
    }

    public SelectChain<T> select(SqlFnExpr<T> expr) {
        return addNode(new SelectField(expr));
    }

    public SelectChain<T> select(SqlFnExpr<T> expr, String alias) {
        return addNode(new SelectField(expr, alias));
    }

    public SelectChain<T> select(String field, String... fields) {
        addNode(new SelectField(field));
        for (String f : fields) {
            addNode(new SelectField(f));
        }
        return this;
    }

    @SafeVarargs
    public final SelectChain<T> select(FieldGetter<T, ?> field, FieldGetter<T, ?>... fields) {
        addNode(new SelectField(new Field(field)));
        for (FieldGetter<T, ?> f : fields) {
            addNode(new SelectField(new Field(f)));
        }
        return this;
    }

    public SelectChain<T> groupBy(String field) {
        return addNode(new GroupBy(new Field(field)));
    }

    public SelectChain<T> groupBy(String field, String... fields) {
        addNode(new GroupBy(new Field(field)));
        for (String f : fields) {
            addNode(new GroupBy(new Field(f)));
        }
        return this;
    }

    public SelectChain<T> groupBy(FieldGetter<T, ?> field) {
        return addNode(new GroupBy(new Field(field)));
    }

    @SafeVarargs
    public final SelectChain<T> groupBy(FieldGetter<T, ?> field, FieldGetter<T, ?>... fields) {
        addNode(new GroupBy(new Field(field)));
        for (FieldGetter<T, ?> f : fields) {
            addNode(new GroupBy(new Field(f)));
        }
        return this;
    }

    public SelectChain<T> having(String expr) {
        return addNode(new Having(expr));
    }

    public SelectChain<T> orderBy(String field) {
        return addNode(new OrderBy(new Field(field), false));
    }

    public SelectChain<T> orderBy(String field, boolean desc) {
        return addNode(new OrderBy(new Field(field), desc));
    }

    public SelectChain<T> orderBy(String field1, boolean desc1, String field2, boolean desc2) {
        return addNode(new OrderBy(new Field(field1), desc1)).addNode(new OrderBy(new Field(field2), desc2));
    }

    public SelectChain<T> orderBy(String field1, boolean desc1, String field2, boolean desc2, String field3, boolean desc3) {
        return addNode(new OrderBy(new Field(field1), desc1)).addNode(new OrderBy(new Field(field2), desc2)).addNode(new OrderBy(new Field(field3), desc3));
    }

    public SelectChain<T> orderBy(FieldGetter<T, ?> field) {
        return addNode(new OrderBy(new Field(field), false));
    }

    public SelectChain<T> orderBy(FieldGetter<T, ?> field, boolean desc) {
        return addNode(new OrderBy(new Field(field), desc));
    }

    public SelectChain<T> orderBy(FieldGetter<T, ?> field1, boolean desc1, FieldGetter<T, ?> field2, boolean desc2) {
        return addNode(new OrderBy(new Field(field1), desc1)).addNode(new OrderBy(new Field(field2), desc2));
    }

    public SelectChain<T> orderBy(FieldGetter<T, ?> field1, boolean desc1, FieldGetter<T, ?> field2, boolean desc2, FieldGetter<T, ?> field3, boolean desc3) {
        return addNode(new OrderBy(new Field(field1), desc1)).addNode(new OrderBy(new Field(field2), desc2)).addNode(new OrderBy(new Field(field3), desc3));
    }

    @Override
    protected String sql() {
        return SelectSqlGenerator.gen(this);
    }
}
