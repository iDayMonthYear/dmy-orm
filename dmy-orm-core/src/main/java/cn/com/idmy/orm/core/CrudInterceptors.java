package cn.com.idmy.orm.core;

import java.util.*;

/**
 * CRUD拦截器管理器
 */
public class CrudInterceptors {
    // 按操作类型分组存储拦截器
    private static final Map<CrudInterceptor.CrudType, List<CrudInterceptor>> typeInterceptors = new EnumMap<>(CrudInterceptor.CrudType.class);

    /**
     * 添加拦截器
     */
    public static void addInterceptor(CrudInterceptor interceptor) {
        // 根据拦截器关心的操作类型分组存储
        for (var type : interceptor.getInterceptTypes()) {
            typeInterceptors.computeIfAbsent(type, k -> new ArrayList<>()).add(interceptor);
        }
    }

    /**
     * 插入前拦截 - 单个实体
     */
    static void interceptInsert(Object entity) {
        var interceptors = typeInterceptors.get(CrudInterceptor.CrudType.INSERT);
        if (interceptors != null) {
            Class<?> entityClass = entity.getClass();
            for (var interceptor : interceptors) {
                if (interceptor.support(entityClass)) {
                    interceptor.beforeInsert(entity);
                }
            }
        }
    }

    /**
     * 插入前拦截 - 实体集合
     */
    static void interceptInsert(Collection<?> entities) {
        if (!entities.isEmpty()) {
            var interceptors = typeInterceptors.get(CrudInterceptor.CrudType.INSERT);
            if (interceptors != null) {
                Class<?> entityClass = entities.iterator().next().getClass();
                for (var interceptor : interceptors) {
                    if (interceptor.support(entityClass)) {
                        interceptor.beforeInsert(entities);
                    }
                }
            }
        }
    }

    /**
     * 更新前拦截 - 使用实体更新
     */
    static void interceptUpdate(Object entity) {
        var interceptors = typeInterceptors.get(CrudInterceptor.CrudType.UPDATE);
        if (interceptors != null) {
            Class<?> entityClass = entity.getClass();
            for (var interceptor : interceptors) {
                if (interceptor.support(entityClass)) {
                    interceptor.beforeUpdate(entity);
                }
            }
        }
    }

    /**
     * 更新前拦截 - 使用条件更新
     */
    static void interceptUpdate(Class<?> entityClass, List<Node> nodes) {
        var interceptors = typeInterceptors.get(CrudInterceptor.CrudType.UPDATE);
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
    static void interceptDelete(Class<?> entityClass, List<Node> nodes) {
        var interceptors = typeInterceptors.get(CrudInterceptor.CrudType.DELETE);
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
    static void interceptSelect(Class<?> entityClass, List<Node> nodes) {
        var interceptors = typeInterceptors.get(CrudInterceptor.CrudType.SELECT);
        if (interceptors != null) {
            for (var interceptor : interceptors) {
                if (interceptor.support(entityClass)) {
                    interceptor.beforeSelect(entityClass, nodes);
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