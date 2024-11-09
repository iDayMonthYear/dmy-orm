package cn.com.idmy.orm.core.query.ast;

import cn.com.idmy.orm.core.query.OrmDao;
import lombok.Getter;
import lombok.experimental.Accessors;


@Getter
@Accessors(fluent = true, chain = false)
public class Update<T> extends RootNode implements Crud {
    protected RootNode root;
    protected Class<T> table;

     Update(Class<T> table) {
        root = this;
        this.table = table;
    }

    public static <T> Update<T> of(OrmDao<T> dao) {
        return new Update<>(dao.entityType());
    }

    public Set<T, Update<T>> set(String field, Object expr) {
        return new Set<>(this, field, expr);
    }

    public Set<T, Update<T>> set(FieldGetter<T, ?> field, Object expr) {
        return new Set<>(this, field, expr);
    }

    public Set<T, Update<T>> set(FieldGetter<T, ?> field, SqlExpression expr) {
        return new Set<>(this, field, expr);
    }
}
