package cn.com.idmy.orm.core.mybatis;

import cn.com.idmy.orm.core.OrmException;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class PreparedParameterHandler extends DefaultParameterHandler {
    private final TypeHandlerRegistry typeHandlerRegistry;

    public PreparedParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        super(mappedStatement, parameterObject, boundSql);
        this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
    }

    @Override
    public void setParameters(PreparedStatement ps) {
        try {
            doSetParameters(ps);
        } catch (SQLException e) {
            throw new OrmException(e);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void doSetParameters(PreparedStatement ps) throws SQLException {
        Object[] sqlArgs;
        Map parameters = (Map) getParameterObject();
        if ((sqlArgs = (Object[]) parameters.get(MybatisConsts.SQL_ARGS)) == null || sqlArgs.length == 0) {
            super.setParameters(ps);
            return;
        }

        int index = 1;
        for (Object value : sqlArgs) {
            if (value == null) {
                ps.setObject(index++, null);
            } else {
                TypeHandler typeHandler = typeHandlerRegistry.getTypeHandler(value.getClass());
                if (typeHandler == null) {
                    typeHandler = typeHandlerRegistry.getUnknownTypeHandler();
                }
                typeHandler.setParameter(ps, index++, value, null);
            }
        }
    }
}
