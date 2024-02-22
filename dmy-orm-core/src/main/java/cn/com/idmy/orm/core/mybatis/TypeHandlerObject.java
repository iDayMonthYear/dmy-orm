package cn.com.idmy.orm.core.mybatis;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public record TypeHandlerObject(TypeHandler typeHandler, Object value,
                                JdbcType jdbcType) implements Serializable {
    public void setParameter(PreparedStatement ps, int idx) throws SQLException {
        typeHandler.setParameter(ps, idx, value, jdbcType);
    }
}
