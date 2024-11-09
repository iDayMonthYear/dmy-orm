package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.query.ast.Delete;
import cn.com.idmy.orm.core.query.ast.Select;
import cn.com.idmy.orm.core.query.ast.Update;

public interface OrmDao<T> {
    Class<T> entityType();

    default boolean delete(Delete<T> delete) {
        return true;
    }

    default boolean update(Update<T> update) {
        return true;
    }

    default Object select(Select<T> select) {
        return null;
    }
}