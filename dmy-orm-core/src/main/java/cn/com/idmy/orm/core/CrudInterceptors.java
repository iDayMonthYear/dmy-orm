package cn.com.idmy.orm.core;


import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * CRUD拦截器管理器
 */
public class CrudInterceptors {
    // 按操作类型分组存储拦截器
    private static final Map<CrudType, List<CrudInterceptor>> typeInterceptors = new EnumMap<>(CrudType.class);

    /**
     * 添加拦截器
     */
    public static void addInterceptor(@NonNull CrudInterceptor interceptor) {
        // 根据拦截器关心的操作类型分组存储
        for (var type : interceptor.interceptTypes()) {
            typeInterceptors.computeIfAbsent(type, k -> new ArrayList<>()).add(interceptor);
        }
    }

    /**
     * 插入前拦截 - 单个实体
     */
    static void interceptCreate(@NotNull Object entity) {
        var interceptors = typeInterceptors.get(CrudType.INSERT);
        if (interceptors != null) {
            var entityClass = entity.getClass();
            for (var interceptor : interceptors) {
                if (interceptor.support(entityClass)) {
                    interceptor.beforeCreate(entity);
                }
            }
        }
    }

    /**
     * 插入前拦截 - 实体集合
     */
    static void interceptCreate(@NonNull Collection<?> entities) {
        if (!entities.isEmpty()) {
            var interceptors = typeInterceptors.get(CrudType.INSERT);
            if (interceptors != null) {
                var entityClass = entities.iterator().next().getClass();
                for (var interceptor : interceptors) {
                    if (interceptor.support(entityClass)) {
                        interceptor.beforeCreate(entities);
                    }
                }
            }
        }
    }

    /**
     * 更新前拦截 - 使用条件更新
     */
    static void interceptUpdate(@NotNull Class<?> entityClass, @NotNull List<SqlNode> nodes) {
        var interceptors = typeInterceptors.get(CrudType.UPDATE);
        if (interceptors != null) {
            for (var interceptor : interceptors) {
                if (interceptor.support(entityClass)) {
                    interceptor.beforeUpdate(entityClass, nodes);
                }
            }
        }
    }

    /**
     * 删除前拦截
     */
    static void interceptDelete(@NotNull Class<?> entityClass, @NotNull List<SqlNode> nodes) {
        var interceptors = typeInterceptors.get(CrudType.DELETE);
        if (interceptors != null) {
            for (var interceptor : interceptors) {
                if (interceptor.support(entityClass)) {
                    interceptor.beforeDelete(entityClass, nodes);
                }
            }
        }
    }

    /**
     * 查询前拦截
     */
    static void interceptQuery(@NotNull Class<?> entityClass, @NotNull List<SqlNode> nodes) {
        var interceptors = typeInterceptors.get(CrudType.SELECT);
        if (interceptors != null) {
            for (var interceptor : interceptors) {
                if (interceptor.support(entityClass)) {
                    interceptor.beforeQuery(entityClass, nodes);
                }
            }
        }
    }

    /**
     * 清空所有拦截器
     */
    public static void clear() {
        typeInterceptors.clear();
    }
}