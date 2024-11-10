package cn.com.idmy.orm.core.ast;

import cn.com.idmy.orm.core.OrmDao;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Accessors(fluent = true, chain = false)
public class SelectChain<T> extends Sud<T, SelectChain<T>> {

    private SelectChain(Class<T> table) {
        super(table);
        sud = this;
    }

    public static <T> SelectChain<T> of(OrmDao<T> dao) {
        return new SelectChain<>(dao.entityType());
    }
/*
    public Select<T> select(String field) {
        return addNode(new SelectField(List.of(field)));
    }

    public Select<T> select(String... exprs) {
        return addNode(new SelectField(List.of(exprs)));
    }

    @SafeVarargs
    public final Select<T> select(FieldGetter<T, ?>... fields) {
        return addNode(new SelectField(List.of(fields)));
    }

    public Select<T> groupBy(String field) {
        return addNode(new GroupBy(List.of(field)));
    }

    public Select<T> groupBy(String field1, String field2, String... fieldN) {
        return addNode(new GroupBy(List.of(field1, field2, fieldN)));
    }

    public Select<T> having(String expr) {
        return addNode(new Having(expr));
    }

    public Select<T> orderBy(String field, boolean desc) {
        return addNode(new OrderBy(field, desc));
    }

    public Select<T> orderBy(FieldGetter<T, ?> field, boolean desc) {
        return addNode(new OrderBy(field, desc));
    }

    public Select<T> orderBy(String field) {
        return addNode(new OrderBy(field, false));
    }

    public Select<T> orderBy(FieldGetter<T, ?> field) {
        return addNode(new OrderBy(field, false));
    }*/

    @Override
    protected String sql() {
       // return SelectSqlGenerator.gen(this);
        return null;
    }

    @Override
    public String toString() {
        try {
            return sql();
        } catch (Exception e) {
            log.warn("SQL生成失败：{}", e.getMessage());
            return null;
        }
    }
}
