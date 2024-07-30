package cn.com.idmy.orm.core.handler;

import cn.com.idmy.orm.annotation.EnumValue;
import cn.com.idmy.orm.core.util.ClassUtil;
import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CompositeEnumTypeHandler<E extends Enum<E>> implements TypeHandler<E> {

    private final TypeHandler<E> delegate;

    public CompositeEnumTypeHandler(Class<E> enumClass) {
        boolean isNotFound = false;
        List<Field> enumDbValueFields = ClassUtil.getAllFields(enumClass, f -> f.getAnnotation(EnumValue.class) != null);
        if (enumDbValueFields.isEmpty()) {
            List<Method> enumDbValueMethods = ClassUtil.getAllMethods(enumClass, m -> m.getAnnotation(EnumValue.class) != null);
            if (enumDbValueMethods.isEmpty()) {
                isNotFound = true;
            }
        }
        if (isNotFound) {
            delegate = new EnumTypeHandler<>(enumClass);
        } else {
            delegate = new FlexEnumTypeHandler<>(enumClass);
        }
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        delegate.setParameter(ps, i, parameter, jdbcType);
    }

    @Override
    public E getResult(ResultSet rs, String columnName) throws SQLException {
        return delegate.getResult(rs, columnName);
    }

    @Override
    public E getResult(ResultSet rs, int columnIndex) throws SQLException {
        return delegate.getResult(rs, columnIndex);
    }

    @Override
    public E getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return delegate.getResult(cs, columnIndex);
    }

}
