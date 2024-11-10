package cn.com.idmy.orm.core;

import cn.com.idmy.orm.core.ast.DeleteChain;
import cn.com.idmy.orm.core.ast.UpdateChain;
import cn.com.idmy.orm.core.ast.UpdateWhere;

public interface OrmDao<T> {
    Class<T> entityType();

    default boolean delete(DeleteChain<T> delete) {
        return true;
    }

    default boolean update(UpdateChain<T> update) {
        return true;
    }

    default boolean update(T entity, UpdateWhere<T> where) {
        where.entity(entity);
        return true;
    }

    default boolean update(T entity, UpdateWhere<T> where, boolean nulls) {
        where.entity(entity);
        return true;
    }
}