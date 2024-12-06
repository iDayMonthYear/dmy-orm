package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.MybatisSqlProvider;
import cn.com.idmy.orm.mybatis.handler.TypeHandlerValue;
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
            var sqlParams = (List<Object>) params.get(MybatisSqlProvider.SQL_PARAMS);
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
        switch (value) {
            case null -> ps.setObject(index, null);
            case Object[] val -> {
                if (val.length == 0) {
                    throw new OrmException("Empty array");
                }
                for (var item : val) {
                    setParameter(ps, index++, item, params);
                }
            }
            case Collection<?> val -> {
                if (val.isEmpty()) {
                    throw new OrmException("Empty list");
                }
                for (var item : val) {
                    setParameter(ps, index++, item, params);
                }
            }
            case TypeHandlerValue val -> val.setParameter(ps, index);
            default -> {
                TypeHandler typeHandler = typeHandlerRegistry.getTypeHandler(value.getClass());
                if (typeHandler == null) {
                    typeHandler = typeHandlerRegistry.getUnknownTypeHandler();
                }
                typeHandler.setParameter(ps, index, value, null);
            }
        }
    }
}