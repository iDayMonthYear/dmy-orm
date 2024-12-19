package cn.com.idmy.orm.core;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * CRUD操作拦截器接口
 */
public interface CrudInterceptor {
    /**
     * 创建前拦截 - 单个实体
     */
    default void beforeCreate(Object entity) {
    }

    /**
     * 创建前拦截 - 实体集合
     */
    default void beforeCreate(Collection<?> entities) {
        entities.forEach(this::beforeCreate);
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
    default void beforeQuery(Class<?> entityClass, List<SqlNode> nodes) {
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
    Set<CrudType> interceptTypes();
}