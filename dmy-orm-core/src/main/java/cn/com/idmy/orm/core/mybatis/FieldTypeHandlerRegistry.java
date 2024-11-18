package cn.com.idmy.orm.core.mybatis;

import cn.com.idmy.orm.core.ast.FieldGetter;
import lombok.Data;
import org.apache.ibatis.type.TypeHandler;
import org.dromara.hutool.core.func.LambdaUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FieldTypeHandlerRegistry {
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
    private static class TypeHandlerKey {
        private final Class<?> entityClass;
        private final String fieldName;

        public TypeHandlerKey(Class<?> entityClass, String fieldName) {
            this.entityClass = entityClass;
            this.fieldName = fieldName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            TypeHandlerKey that = (TypeHandlerKey) o;
            return entityClass.equals(that.entityClass) && fieldName.equals(that.fieldName);
        }

        @Override
        public int hashCode() {
            return 31 * entityClass.hashCode() + fieldName.hashCode();
        }
    }
}