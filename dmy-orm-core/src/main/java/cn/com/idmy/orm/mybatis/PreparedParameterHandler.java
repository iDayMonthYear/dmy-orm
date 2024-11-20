package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.AbstractWhere;
import cn.com.idmy.orm.core.TableManager;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.dromara.hutool.core.collection.CollUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
class PreparedParameterHandler extends DefaultParameterHandler {
    private final TypeHandlerRegistry typeHandlerRegistry;

    public PreparedParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        super(mappedStatement, parameterObject, boundSql);
        this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public void setParameters(PreparedStatement ps) {
        try {
            var params = (Map<String, Object>) getParameterObject();
            var sqlParams = (List<Object>) params.get(MybatisConsts.SQL_PARAMS);
            if (sqlParams == null) {
                super.setParameters(ps);
            } else {
                for (int i = 0, size = sqlParams.size(); i < size; i++) {
                    setParameter(ps, i + 1, sqlParams.get(i), params);
                }
            }
        } catch (SQLException e) {
            throw new OrmException(e);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void setParameter(PreparedStatement ps, int index, Object value, Map<String, Object> params) throws SQLException {
        if (value == null) {
            ps.setObject(index, null);
        } else if (value.getClass().isArray()) {
            var array = (Object[]) value;
            for (var item : array) {
                setParameter(ps, index++, item, params);
            }
        } else if (value instanceof Collection<?> collection) {
            for (var item : collection) {
                setParameter(ps, index++, item, params);
            }
        } else {
            TypeHandler typeHandler = getTypeHandler(value, params);
            // 此处的 jdbcType 可以为 null 的，原因是 value 不为 null，
            // 只有 value 为 null 时， jdbcType 不允许为 null
            typeHandler.setParameter(ps, index, value, null);
        }
    }

    private TypeHandler<?> getTypeHandler(Object value, Map<String, Object> params) {
        var valueType = value.getClass();
        var entityClass = getEntityClass(params);

        if (entityClass != null) {
            var customHandler = TableManager.getHandler(entityClass, valueType.getName());
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
    public static Class<?> getEntityClass(Map<String, Object> params) {
        var chain = params.get(MybatisConsts.CHAIN);
        if (chain instanceof AbstractWhere<?, ?> where) {
            return where.entityClass();
        } else {
            var entity = params.get(MybatisConsts.ENTITY);
            if (entity == null) {
                var entities = (Collection<?>) params.get(MybatisConsts.ENTITIES);
                if (CollUtil.isEmpty(entities)) {
                    return null;
                } else {
                    return entities.iterator().next().getClass();
                }
            } else {
                return entity.getClass();
            }
        }
    }
}