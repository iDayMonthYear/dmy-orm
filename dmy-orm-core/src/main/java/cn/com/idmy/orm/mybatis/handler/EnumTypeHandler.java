package cn.com.idmy.orm.mybatis.handler;

import cn.com.idmy.base.annotation.EnumValue;
import jakarta.annotation.Nullable;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {
    private static final Map<Class<?>, Field> enumValueFieldCache = new ConcurrentHashMap<>();
    private final Class<E> type;
    private final Field valueField;

    public EnumTypeHandler(Class<E> type) {
        this.type = type;
        this.valueField = getEnumValueField(type);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int idx, E parameter, JdbcType jdbcType) throws SQLException {
        if (valueField == null) {
            ps.setString(idx, parameter.name());
        } else {
            try {
                ps.setObject(idx, valueField.get(parameter));
            } catch (IllegalAccessException e) {
                throw new SQLException(e);
            }
        }
    }

    @Override
    public E getNullableResult(ResultSet rs, String name) throws SQLException {
        return rs.wasNull() ? null : valueOf(rs.getObject(name));
    }

    @Override
    public E getNullableResult(ResultSet rs, int idx) throws SQLException {
        return rs.wasNull() ? null : valueOf(rs.getObject(idx));
    }

    @Override
    public E getNullableResult(CallableStatement cs, int idx) throws SQLException {
        return cs.wasNull() ? null : valueOf(cs.getObject(idx));
    }

    @Nullable
    private Field getEnumValueField(Class<E> type) {
        return enumValueFieldCache.computeIfAbsent(type, $ -> {
            for (Field field : type.getDeclaredFields()) {
                if (field.isAnnotationPresent(EnumValue.class)) {
                    field.setAccessible(true);
                    return field;
                }
            }
            return null;
        });
    }

    private E valueOf(Object value) throws SQLException {
        if (valueField == null) {
            return Enum.valueOf(type, value.toString());
        } else {
            for (E enumConstant : type.getEnumConstants()) {
                try {
                    if (value.equals(valueField.get(enumConstant))) {
                        return enumConstant;
                    }
                } catch (IllegalAccessException e) {
                    throw new SQLException(e);
                }
            }
            throw new SQLException("Cannot convert " + value + " to " + type.getSimpleName());
        }
    }
}