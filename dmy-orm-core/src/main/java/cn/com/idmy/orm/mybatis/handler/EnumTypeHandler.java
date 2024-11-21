package cn.com.idmy.orm.mybatis.handler;

import cn.com.idmy.orm.annotation.EnumValue;
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
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        if (valueField != null) {
            try {
                ps.setObject(i, valueField.get(parameter));
            } catch (IllegalAccessException e) {
                throw new SQLException(e);
            }
        } else {
            ps.setString(i, parameter.name());
        }
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object value = rs.getObject(columnName);
        return rs.wasNull() ? null : valueOf(value);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object value = rs.getObject(columnIndex);
        return rs.wasNull() ? null : valueOf(value);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object value = cs.getObject(columnIndex);
        return cs.wasNull() ? null : valueOf(value);
    }

    @Nullable
    private Field getEnumValueField(Class<E> enumType) {
        return enumValueFieldCache.computeIfAbsent(enumType, k -> {
            for (Field field : enumType.getDeclaredFields()) {
                if (field.isAnnotationPresent(EnumValue.class)) {
                    field.setAccessible(true);
                    return field;
                }
            }
            return null;
        });
    }

    private E valueOf(Object value) throws SQLException {
        if (valueField != null) {
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
        return Enum.valueOf(type, value.toString());
    }
}