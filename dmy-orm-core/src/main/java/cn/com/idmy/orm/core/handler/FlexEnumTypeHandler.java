package cn.com.idmy.orm.core.handler;

import cn.com.idmy.orm.core.util.EnumWrapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FlexEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {
    private final EnumWrapper<E> enumWrapper;

    public FlexEnumTypeHandler(Class<E> enumClass) {
        enumWrapper = EnumWrapper.of(enumClass);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        Object value = enumWrapper.getEnumValue(parameter);
        if (jdbcType == null) {
            ps.setObject(i, value);
        } else {
            ps.setObject(i, value, jdbcType.TYPE_CODE);
        }
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object value = rs.getObject(columnName, enumWrapper.getPropertyType());
        if (null == value && rs.wasNull()) {
            return null;
        }
        return enumWrapper.getEnum(value);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object value = rs.getObject(columnIndex, enumWrapper.getPropertyType());
        if (null == value && rs.wasNull()) {
            return null;
        }
        return enumWrapper.getEnum(value);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object value = cs.getObject(columnIndex, enumWrapper.getPropertyType());
        if (null == value && cs.wasNull()) {
            return null;
        }
        return enumWrapper.getEnum(value);
    }

}
