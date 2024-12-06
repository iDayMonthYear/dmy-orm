package cn.com.idmy.orm.mybatis.handler;

import com.alibaba.fastjson2.JSONArray;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(JSONArray.class)
public class JsonArrayTypeHandler extends BaseTypeHandler<JSONArray> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int idx, JSONArray params, JdbcType jdbcType) throws SQLException {
        ps.setString(idx, params.toString());
    }

    @Override
    public JSONArray getNullableResult(ResultSet rs, String name) throws SQLException {
        return rs.wasNull() ? null : JSONArray.parse(rs.getString(name));
    }

    @Override
    public JSONArray getNullableResult(ResultSet rs, int idx) throws SQLException {
        return rs.wasNull() ? null : JSONArray.parse(rs.getString(idx));
    }

    @Override
    public JSONArray getNullableResult(CallableStatement cs, int idx) throws SQLException {
        return cs.wasNull() ? null : JSONArray.parse(cs.getString(idx));
    }
} 