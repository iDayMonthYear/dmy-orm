package cn.com.idmy.orm.core.query.ast;

import cn.com.idmy.orm.core.query.OrmDao;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true, chain = false)
public class Delete<T> extends RootNode implements Crud {
    protected RootNode root;
    protected Class<T> table;

    Delete(Class<T> table) {
        root = this;
        this.table = table;
    }

    public static <T> Delete<T> of(OrmDao<T> dao) {
        return new Delete<>(dao.entityType());
    }

    public From<T, Delete<T>> from() {
        return new From<>(this);
    }
}
