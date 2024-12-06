package cn.com.idmy.orm.mybatis.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes({Object.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
@RequiredArgsConstructor
public class JsonTypeHandler<T> extends BaseTypeHandler<T> {
    private final TypeReference<T> type;

    @Override
    public void setNonNullParameter(PreparedStatement ps, int idx, T param, JdbcType jdbcType) throws SQLException {
        ps.setString(idx, JSON.toJSONString(param));
    }

    @Override
    public T getNullableResult(ResultSet rs, String name) throws SQLException {
        return rs.wasNull() ? null : parseJson(rs.getString(name));
    }

    @Override
    public T getNullableResult(ResultSet rs, int idx) throws SQLException {
        return rs.wasNull() ? null : parseJson(rs.getString(idx));
    }

    @Override
    public T getNullableResult(CallableStatement cs, int idx) throws SQLException {
        return cs.wasNull() ? null : parseJson(cs.getString(idx));
    }

    private T parseJson(String json) {
        return JSON.parseObject(json, type);
    }
}