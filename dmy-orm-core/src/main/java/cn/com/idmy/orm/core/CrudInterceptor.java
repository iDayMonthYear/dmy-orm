package cn.com.idmy.orm.core;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * CRUD操作拦截器接口
 */
public interface CrudInterceptor {
    /**
     * 插入前拦截 - 单个实体
     */
    default void beforeInsert(Object entity) {
    }

    /**
     * 插入前拦截 - 实体集合
     */
    default void beforeInsert(Collection<?> entities) {
        entities.forEach(this::beforeInsert);
    }

    /**
     * 更新前拦截 - 使用实体更新
     */
    default void beforeUpdate(Object entity) {
    }

    /**
     * 更新前拦截 - 使用条件更新
     */
    default void beforeUpdate(Class<?> entityClass, List<SqlNode> nodes) {
    }

    /**
     * 删除前拦截
     */
    default void beforeDelete(Class<?> entityClass, List<SqlNode> nodes) {
    }

    /**
     * 查询前拦截
     */
    default void beforeSelect(Class<?> entityClass, List<SqlNode> nodes) {
    }

    /**
     * 是否拦截该实体类
     */
    default boolean support(Class<?> entityClass) {
        return true;
    }

    /**
     * 获取拦截器关心的操作类型
     */
    Set<CrudType> getInterceptTypes();
}