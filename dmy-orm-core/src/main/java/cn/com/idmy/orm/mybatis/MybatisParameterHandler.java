package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.TableManager;
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

@Slf4j
class MybatisParameterHandler extends DefaultParameterHandler {
    private final TypeHandlerRegistry typeHandlerRegistry;

    public MybatisParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
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
            typeHandler.setParameter(ps, index, value, null);
        }
    }

    private TypeHandler<?> getTypeHandler(Object value, Map<String, Object> params) {
        var valueType = value.getClass();
        var entityClass = MybatisConsts.getEntityClass(params);
        var customHandler = TableManager.getHandler(entityClass, valueType.getName());
        if (customHandler != null) {
            try {
                return customHandler.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                log.warn("Failed to create custom type handler", e);
            }
        }
        return typeHandlerRegistry.getTypeHandler(valueType);
    }
}