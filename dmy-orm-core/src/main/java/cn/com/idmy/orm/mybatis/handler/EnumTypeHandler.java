package cn.com.idmy.orm.mybatis.handler;

import cn.com.idmy.base.annotation.EnumValue;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class EnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {
    private static final Map<Class<?>, Field> enumValueFieldCache = new ConcurrentHashMap<>(1);
    private final Class<E> type;
    @Nullable
    private final Field valueField;

    public EnumTypeHandler(@NotNull Class<E> type) {
        this.type = type;
        this.valueField = getEnumValueField(type);
    }

    @Override
    public void setNonNullParameter(@NotNull PreparedStatement ps, int idx, @NotNull E param, @NotNull JdbcType jdbcType) throws SQLException {
        if (valueField == null) {
            ps.setObject(idx, param);
        } else {
            try {
                ps.setObject(idx, valueField.get(param));
            } catch (IllegalAccessException e) {
                throw new SQLException(e);
            }
        }
    }

    @Override
    public E getNullableResult(@NotNull ResultSet rs, @NotNull String name) throws SQLException {
        return rs.wasNull() ? null : valueOf(rs.getObject(name));
    }

    @Override
    public E getNullableResult(@NotNull ResultSet rs, int idx) throws SQLException {
        return rs.wasNull() ? null : valueOf(rs.getObject(idx));
    }

    @Override
    public E getNullableResult(@NotNull CallableStatement cs, int idx) throws SQLException {
        return cs.wasNull() ? null : valueOf(cs.getObject(idx));
    }

    private Field getEnumValueField(@NotNull Class<E> type) {
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
                    if (Objects.equals(value, valueField.get(enumConstant))) {
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