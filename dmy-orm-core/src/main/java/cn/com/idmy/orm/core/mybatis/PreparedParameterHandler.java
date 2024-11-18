package cn.com.idmy.orm.core.mybatis;

import cn.com.idmy.orm.core.OrmException;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PreparedParameterHandler extends DefaultParameterHandler {
    private final TypeHandlerRegistry typeHandlerRegistry;
    private final MappedStatement mappedStatement;

    public PreparedParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        super(mappedStatement, parameterObject, boundSql);
        this.mappedStatement = mappedStatement;
        this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
    }

    @Override
    public void setParameters(PreparedStatement ps) {
        try {
            Object[] sqlArgs;
            Map parameters = (Map) getParameterObject();
            if ((sqlArgs = (Object[]) parameters.get(MybatisConsts.SQL_ARGS)) == null) {
                super.setParameters(ps);
                return;
            }

            int index = 1;
            for (Object value : sqlArgs) {
                setParameter(ps, index++, value);
            }
        } catch (SQLException e) {
            throw new OrmException(e);
        }
    }

    /**
     * 设置PreparedStatement参数
     *
     * @param ps    PreparedStatement实例
     * @param index 参数索引
     * @param value 参数值
     * @throws SQLException SQL异常
     */
    private void setParameter(PreparedStatement ps, int index, Object value) throws SQLException {
        // 处理null值
        if (value == null) {
            ps.setObject(index, null);
            return;
        }

        // 处理集合类型 - 将集合展开为多个参数
        if (value instanceof Collection<?> collection) {
            for (Object item : collection) {
                setParameter(ps, index++, item);
            }
            return;
        }

        // 处理数组类型 - 将数组展开为多个参数
        if (value.getClass().isArray()) {
            Object[] array = (Object[]) value;
            for (Object item : array) {
                setParameter(ps, index++, item);
            }
            return;
        }

        // 获取并使用TypeHandler设置参数值
        // 优先级:
        // 1. 从缓存获取已创建的TypeHandler
        // 2. 获取字段级别的TypeHandler(如果实体类配置了TypeHandler)
        // 3. 获取类型级别的TypeHandler
        // 4. 使用默认的UnknownTypeHandler
        TypeHandler typeHandler = getTypeHandler(value);
        typeHandler.setParameter(ps, index, value, null);
    }

    // 只缓存有TypeHandler配置的实体类
    private static final Map<String, Class<?>> ENTITY_CLASS_CACHE = new ConcurrentHashMap<>();

    // 只缓存实际使用的TypeHandler
    private static final Map<Class<?>, TypeHandler<?>> TYPE_HANDLER_CACHE = new ConcurrentHashMap<>();

    private TypeHandler<?> getTypeHandler(Object value) {
        Class<?> valueType = value.getClass();

        // 1. 先从类型级别缓存获取
        TypeHandler<?> cachedHandler = TYPE_HANDLER_CACHE.get(valueType);
        if (cachedHandler != null) {
            return cachedHandler;
        }

        // 2. 检查字段级别的类型处理器
        String msId = mappedStatement.getId();
        String entityClassName = msId.substring(0, msId.lastIndexOf("."));

        // 只有配置了TypeHandler的实体类才会被缓存
        Class<?> entityClass = ENTITY_CLASS_CACHE.get(entityClassName);
        if (entityClass == null) {
            try {
                Class<?> cls = Class.forName(entityClassName);
                // 检查是否有TypeHandler配置
                if (FieldTypeHandlerRegistry.hasHandlers(cls)) {
                    ENTITY_CLASS_CACHE.put(entityClassName, cls);
                    entityClass = cls;
                }
            } catch (ClassNotFoundException ignored) {
            }
        }

        if (entityClass != null) {
            Class<? extends TypeHandler<?>> handlerClass = FieldTypeHandlerRegistry.getHandler(entityClass, valueType.getName());
            if (handlerClass != null) {
                try {
                    TypeHandler<?> handler = handlerClass.getDeclaredConstructor().newInstance();
                    // 缓存字段级别的处理器
                    TYPE_HANDLER_CACHE.put(valueType, handler);
                    return handler;
                } catch (Exception ignored) {
                }
            }
        }

        // 3. 获取类型级别的处理器
        TypeHandler<?> typeHandler = typeHandlerRegistry.getTypeHandler(valueType);
        if (typeHandler != null) {
            // 缓存类型级别的处理器
            TYPE_HANDLER_CACHE.put(valueType, typeHandler);
            return typeHandler;
        }

        // 4. 使用默认处理器(不缓存)
        return typeHandlerRegistry.getUnknownTypeHandler();
    }
}