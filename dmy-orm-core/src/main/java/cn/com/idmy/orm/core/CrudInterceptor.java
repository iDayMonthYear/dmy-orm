package cn.com.idmy.orm.core;

import org.jetbrains.annotations.NotNull;

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
    default void beforeCreate(@NotNull Object entity) {
    }

    /**
     * 创建前拦截 - 实体集合
     */
    default void beforeCreate(@NotNull Collection<?> ls) {
        ls.forEach(this::beforeCreate);
    }

    /**
     * 更新前拦截 - 使用条件更新
     */
    default void beforeUpdate(@NotNull Class<?> entityType, @NotNull List<SqlNode> nodes) {
    }

    /**
     * 删除前拦截
     */
    default void beforeDelete(@NotNull Class<?> entityType, @NotNull List<SqlNode> nodes) {
    }

    /**
     * 查询前拦截
     */
    default void beforeQuery(@NotNull Class<?> entityType, @NotNull List<SqlNode> nodes) {
    }

    /**
     * 是否拦截该实体类
     */
    default boolean support(@NotNull Class<?> entityType) {
        return true;
    }

    /**
     * 获取拦截器关心的操作类型
     */
    @NotNull
    Set<CrudType> interceptTypes();
}