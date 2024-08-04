package cn.com.idmy.orm.core.mask;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CompositeMaskTypeHandler implements TypeHandler<Object> {
    private final String maskType;
    private final TypeHandler<Object> typeHandler;

    public CompositeMaskTypeHandler(String maskType, TypeHandler<Object> typeHandler) {
        this.maskType = maskType;
        this.typeHandler = typeHandler;
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        typeHandler.setParameter(ps, i, parameter, jdbcType);
    }

    @Override
    public Object getResult(ResultSet rs, String columnName) throws SQLException {
        return MaskManager.mask(maskType, typeHandler.getResult(rs, columnName));
    }

    @Override
    public Object getResult(ResultSet rs, int columnIndex) throws SQLException {
        return MaskManager.mask(maskType, typeHandler.getResult(rs, columnIndex));
    }

    @Override
    public Object getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return MaskManager.mask(maskType, typeHandler.getResult(cs, columnIndex));
    }
}
