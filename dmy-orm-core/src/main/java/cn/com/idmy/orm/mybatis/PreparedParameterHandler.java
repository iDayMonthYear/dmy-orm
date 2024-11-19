package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.OrmException;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
class PreparedParameterHandler extends DefaultParameterHandler {
    private final TypeHandlerRegistry typeHandlerRegistry;
    private final MappedStatement mappedStatement;

    public PreparedParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        super(mappedStatement, parameterObject, boundSql);
        this.mappedStatement = mappedStatement;
        this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void setParameters(PreparedStatement ps) {
        try {
            Map map = (Map) getParameterObject();
            List<Object> sqlParams = (List<Object>) map.get(MybatisConsts.SQL_PARAMS);
            if (sqlParams == null) {
                super.setParameters(ps);
            } else {
                for (int i = 0, size = sqlParams.size(); i < size; i++) {
                    setParameter(ps, i + 1, sqlParams.get(i));
                }
            }
        } catch (SQLException e) {
            throw new OrmException(e);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void setParameter(PreparedStatement ps, int index, Object value) throws SQLException {
        if (value == null) {
            ps.setObject(index, null);
        } else if (value.getClass().isArray()) {
            Object[] array = (Object[]) value;
            for (Object item : array) {
                setParameter(ps, index++, item);
            }
        } else if (value instanceof Collection<?> collection) {
            for (Object item : collection) {
                setParameter(ps, index++, item);
            }
        } else {
            TypeHandler typeHandler = getTypeHandler(value);
            // 此处的 jdbcType 可以为 null 的，原因是 value 不为 null，
            // 只有 value 为 null 时， jdbcType 不允许为 null
            typeHandler.setParameter(ps, index, value, null);
        }
    }

    private static final Map<String, Class<?>> ENTITY_CLASS_CACHE = new ConcurrentHashMap<>();

    private TypeHandler<?> getTypeHandler(Object value) {
        Class<?> valueType = value.getClass();

        String msId = mappedStatement.getId();
        String entityClassName = msId.substring(0, msId.lastIndexOf("."));
        Class<?> entityClass = getEntityClass(entityClassName);

        if (entityClass != null) {
            Class<? extends TypeHandler<?>> customHandler = CustomTypeHandlerRegistry.getHandler(entityClass, valueType.getName());
            if (customHandler != null) {
                try {
                    return customHandler.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    log.warn("Failed to create custom type handler", e);
                }
            }
        }

        return typeHandlerRegistry.getTypeHandler(valueType);
    }

    @Nullable
    private Class<?> getEntityClass(String className) {
        return ENTITY_CLASS_CACHE.computeIfAbsent(className, key -> {
            try {
                Class<?> cls = Class.forName(key);
                return CustomTypeHandlerRegistry.hasHandlers(cls) ? cls : null;
            } catch (ClassNotFoundException e) {
                log.warn("Failed to load entity class: {}", key, e);
                return null;
            }
        });
    }
}