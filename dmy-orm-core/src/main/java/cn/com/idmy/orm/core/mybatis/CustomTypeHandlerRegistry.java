package cn.com.idmy.orm.core.mybatis;

import cn.com.idmy.orm.core.ast.FieldGetter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.type.TypeHandler;
import org.dromara.hutool.core.func.LambdaUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomTypeHandlerRegistry {
    private static final Map<TypeHandlerKey, Class<? extends TypeHandler<?>>> TYPE_HANDLERS = new ConcurrentHashMap<>();

    public static <T, R> void register(Class<T> entityClass, FieldGetter<T, R> field, Class<? extends TypeHandler<?>> handlerClass) {
        String fieldName = LambdaUtil.getFieldName(field);
        TYPE_HANDLERS.put(new TypeHandlerKey(entityClass, fieldName), handlerClass);
    }

    public static Class<? extends TypeHandler<?>> getHandler(Class<?> entityClass, String fieldName) {
        return TYPE_HANDLERS.get(new TypeHandlerKey(entityClass, fieldName));
    }

    public static boolean hasHandlers(Class<?> entityClass) {
        // 检查该实体类是否配置了任何TypeHandler
        return TYPE_HANDLERS.keySet().stream().anyMatch(key -> key.getEntityClass().equals(entityClass));
    }

    public static void clear() {
        TYPE_HANDLERS.clear();
    }

    @Data
    @RequiredArgsConstructor
    @EqualsAndHashCode(of = {"entityClass", "fieldName"})
    private static class TypeHandlerKey {
        private final Class<?> entityClass;
        private final String fieldName;
    }
}