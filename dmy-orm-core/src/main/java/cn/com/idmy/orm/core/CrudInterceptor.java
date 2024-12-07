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
        // 默认遍历调用单个实体的处理方法
        for (Object entity : entities) {
            beforeInsert(entity);
        }
    }

    /**
     * 更新前拦截 - 使用实体更新
     */
    default void beforeUpdate(Object entity) {
    }

    /**
     * 更新前拦截 - 使用条件更新
     */
    default void beforeUpdate(Class<?> entityClass, List<Node> nodes) {
    }

    /**
     * 删除前拦截
     */
    default void beforeDelete(Class<?> entityClass, List<Node> nodes) {
    }

    /**
     * 查询前拦截
     */
    default void beforeSelect(Class<?> entityClass, List<Node> nodes) {
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

    enum CrudType {
        /** 插入操作 */
        INSERT,
        /** 更新操作 */
        UPDATE,
        /** 删除操作 */
        DELETE,
        /** 查询操作 */
        SELECT
    }
}