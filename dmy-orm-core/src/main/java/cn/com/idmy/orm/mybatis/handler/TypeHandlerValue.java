package cn.com.idmy.orm.mybatis.handler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@SuppressWarnings({"rawtypes", "unchecked"})
public record TypeHandlerValue(TypeHandler handler, Object value) implements Serializable {
    public void setParameter(PreparedStatement ps, int idx) throws SQLException {
        handler.setParameter(ps, idx, value, JdbcType.VARCHAR);
    }
}