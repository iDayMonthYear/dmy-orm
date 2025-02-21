package cn.com.idmy.orm.mybatis.handler;

import cn.com.idmy.base.IEnum;
import cn.com.idmy.base.annotation.EnumValue;
import cn.com.idmy.orm.OrmConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
public class EnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {
    private static final Map<Class<?>, Field> enumValueFieldCache = new ConcurrentHashMap<>(1);
    private final Class<E> type;
    @Nullable
    private final Field valueField;
    private final boolean isIEnum;
    private final boolean iEnumValueEnabled;

    public EnumTypeHandler(@NotNull Class<E> type) {
        this.type = type;
        this.isIEnum = IEnum.class.isAssignableFrom(type);
        this.iEnumValueEnabled = OrmConfig.config().iEnumValueEnabled();
        this.valueField = getEnumValueField(type);
    }

    @SneakyThrows
    @Override
    public void setNonNullParameter(@NotNull PreparedStatement ps, int idx, @NotNull E param, @NotNull JdbcType jdbcType) {
        if (valueField != null) {
            ps.setObject(idx, valueField.get(param));
        } else if (iEnumValueEnabled && isIEnum) {
            ps.setObject(idx, ((IEnum<?>) param).value());
        } else {
            ps.setObject(idx, param.name());
        }
    }

    @Override
    public E getNullableResult(@NotNull ResultSet rs, @NotNull String idx) throws SQLException {
        Object object = rs.getObject(idx);
        if (object == null || rs.wasNull()) {
            return null;
        } else {
            return valueOf(object);
        }
    }

    @Override
    public E getNullableResult(@NotNull ResultSet rs, int idx) throws SQLException {
        Object object = rs.getObject(idx);
        if (object == null || rs.wasNull()) {
            return null;
        } else {
            return valueOf(object);
        }
    }

    @Override
    public E getNullableResult(@NotNull CallableStatement cs, int idx) throws SQLException {
        Object object = cs.getObject(idx);
        if (object == null || cs.wasNull()) {
            return null;
        } else {
            return valueOf(object);
        }
    }

    private @Nullable Field getEnumValueField(@NotNull Class<E> type) {
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
        if (isIEnum && iEnumValueEnabled) {
            for (E enumConstant : type.getEnumConstants()) {
                if (Objects.equals(value, ((IEnum<?>) enumConstant).value())) {
                    return enumConstant;
                }
            }
            throw new SQLException("Cannot convert " + value + " to " + type.getSimpleName());
        } else if (valueField == null) {
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