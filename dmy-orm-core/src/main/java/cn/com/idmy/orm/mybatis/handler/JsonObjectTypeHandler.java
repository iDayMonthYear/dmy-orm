package cn.com.idmy.orm.mybatis.handler;

import com.alibaba.fastjson2.JSONObject;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(JSONObject.class)
public class JsonObjectTypeHandler extends BaseTypeHandler<JSONObject> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int idx, JSONObject param, JdbcType jdbcType) throws SQLException {
        ps.setString(idx, param.toString());
    }

    @Override
    public JSONObject getNullableResult(ResultSet rs, String name) throws SQLException {
        return rs.wasNull() ? null : JSONObject.parse(rs.getString(name));
    }

    @Override
    public JSONObject getNullableResult(ResultSet rs, int idx) throws SQLException {
        return rs.wasNull() ? null : JSONObject.parse(rs.getString(idx));
    }

    @Override
    public JSONObject getNullableResult(CallableStatement cs, int idx) throws SQLException {
        return cs.wasNull() ? null : JSONObject.parse(cs.getString(idx));
    }
} 