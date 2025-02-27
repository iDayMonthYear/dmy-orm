package cn.com.idmy.orm.mybatis.handler;


import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@SuppressWarnings({"rawtypes", "unchecked"})
public record TypeHandlerValue(@NotNull TypeHandler handler, @NotNull Object value) implements Serializable {
    public void setParameter(@NotNull PreparedStatement ps, int idx) throws SQLException {
        handler.setParameter(ps, idx, value, JdbcType.VARCHAR);
    }
}