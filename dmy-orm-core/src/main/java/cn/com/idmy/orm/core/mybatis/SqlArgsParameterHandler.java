package cn.com.idmy.orm.core.mybatis;

import cn.com.idmy.orm.core.OrmConsts;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

public class SqlArgsParameterHandler extends DefaultParameterHandler {
    private final Map<?, ?> parameterObject;

    public SqlArgsParameterHandler(MappedStatement mappedStatement, Map<?, ?> parameterObject, BoundSql boundSql) {
        super(mappedStatement, parameterObject, boundSql);
        this.parameterObject = parameterObject;
    }

    @Override
    public void setParameters(PreparedStatement ps) {
        try {
            doSetParameters(ps);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void doSetParameters(PreparedStatement ps) throws SQLException {
        Object[] sqlArgs = (Object[]) parameterObject.get(OrmConsts.SQL_ARGS);
        if (sqlArgs != null && sqlArgs.length > 0) {
            int index = 1;
            for (Object value : sqlArgs) {
                if (value instanceof TypeHandlerObject) {
                    ((TypeHandlerObject) value).setParameter(ps, index++);
                } else if (value instanceof Date) {
                    setDateParameter(ps, (Date) value, index++);
                } else if (value instanceof byte[]) {
                    ps.setBytes(index++, (byte[]) value);
                } else {
                    ps.setObject(index++, value);
                }
            }
        } else {
            super.setParameters(ps);
        }
    }

    /**
     * Oracle、SqlServer 需要主动设置下 date 类型
     * MySql 通过 setObject 后会自动转换，具体查看 MySql 驱动源码
     *
     * @param ps    PreparedStatement
     * @param value date value
     * @param index set to index
     */
    private void setDateParameter(PreparedStatement ps, Date value, int index) throws SQLException {
        if (value instanceof java.sql.Date) {
            ps.setDate(index, (java.sql.Date) value);
        } else if (value instanceof java.sql.Timestamp) {
            ps.setTimestamp(index, (java.sql.Timestamp) value);
        } else {
            ps.setTimestamp(index, new java.sql.Timestamp(value.getTime()));
        }
    }
}
