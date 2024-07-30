package cn.com.idmy.orm.core.mybatis;

import cn.com.idmy.orm.core.OrmConsts;
import cn.com.idmy.orm.core.exception.OrmExceptions;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
 * 向 {@link PreparedStatement} 中的占位符设置值。
 *
 * @author michael
 * @author 王帅
 */
public class SqlArgsParameterHandler extends DefaultParameterHandler {

    private final TypeHandlerRegistry typeHandlerRegistry;

    public SqlArgsParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        super(mappedStatement, parameterObject, boundSql);
        this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
    }

    @Override
    public void setParameters(PreparedStatement ps) {
        try {
            doSetParameters(ps);
        } catch (SQLException e) {
            throw OrmExceptions.wrap(e);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void doSetParameters(PreparedStatement ps) throws SQLException {
        Object[] sqlArgs;
        Map parameters = (Map) getParameterObject();
        if (parameters.containsKey(OrmConsts.RAW_ARGS)
                || (sqlArgs = (Object[]) parameters.get(OrmConsts.SQL_ARGS)) == null
                || sqlArgs.length == 0) {
            super.setParameters(ps);
            return;
        }

        int index = 1;
        for (Object value : sqlArgs) {
            // 设置 NULL 值
            if (value == null) {
                // ps.setNull(index++, Types.NULL);
                // 此处不应该使用 setNull(index++, Types.NULL)，通过 setObject 传入 null 值，有 jdbc 驱动自行验证类型即可
                // 使用 setNull 在 db2 等数据库下，Types.NULL 并非其需要类型
                ps.setObject(index++, null);
                continue;
            }

            // 通过配置的 TypeHandler 去设置值
            if (value instanceof TypeHandlerObject) {
                ((TypeHandlerObject) value).setParameter(ps, index++);
                continue;
            }

            TypeHandler typeHandler = typeHandlerRegistry.getTypeHandler(value.getClass());
            if (typeHandler == null) {
                typeHandler = typeHandlerRegistry.getUnknownTypeHandler();
            }

            // 此处的 jdbcType 可以为 null 的，原因是 value 不为 null，
            // 只有 value 为 null 时， jdbcType 不允许为 null
            typeHandler.setParameter(ps, index++, value, null);
        }
    }

}
