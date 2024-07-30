package cn.com.idmy.orm.core.mybatis;

import lombok.Getter;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TypeHandlerObject implements Serializable {

    private final TypeHandler typeHandler;
    @Getter
    private final Object value;
    private final JdbcType jdbcType;

    public TypeHandlerObject(TypeHandler typeHandler, Object value, JdbcType jdbcType) {
        this.typeHandler = typeHandler;
        this.value = value;
        this.jdbcType = jdbcType;
    }

    public void setParameter(PreparedStatement ps, int i) throws SQLException {
        typeHandler.setParameter(ps, i, value, jdbcType);
    }

    @Override
    public String toString() {
        return "TypeHandlerObject{"
                + "value=" + value
                + ", typeHandler=" + typeHandler.getClass().getSimpleName()
                + '}';
    }
}
