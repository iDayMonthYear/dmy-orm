package cn.com.idmy.orm.core.mask;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
public class MaskTypeHandler extends BaseTypeHandler<Object> {
    private final String maskType;

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.toString());
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String data = rs.getString(columnName);
        return MaskManager.mask(maskType, data);
    }

    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String data = rs.getString(columnIndex);
        return MaskManager.mask(maskType, data);
    }

    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String data = cs.getString(columnIndex);
        return MaskManager.mask(maskType, data);
    }
}
