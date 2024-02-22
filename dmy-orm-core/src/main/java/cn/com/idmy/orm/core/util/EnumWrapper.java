package cn.com.idmy.orm.core.util;

import cn.com.idmy.orm.annotation.EnumValue;
import cn.com.idmy.orm.core.exception.OrmExceptions;
import lombok.Getter;
import org.apache.ibatis.util.MapUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EnumWrapper<E extends Enum<E>> {
    private static final Map<Class, EnumWrapper> cache = new ConcurrentHashMap<>();
    private boolean hasEnumValueAnnotation = false;
    @Getter
    private Class<?> enumClass;
    @Getter
    private E[] enums;
    @Getter
    private Field property;
    @Getter
    private Class<?> propertyType;
    @Getter
    private Method getterMethod;

    public static <R extends Enum<R>> EnumWrapper<R> of(Class<?> enumClass) {
        return MapUtil.computeIfAbsent(cache, enumClass, EnumWrapper::new);
    }

    public EnumWrapper(Class<E> enumClass) {
        this.enumClass = enumClass;
        this.enums = enumClass.getEnumConstants();

        Field enumValueField = ClassUtil.getFirstField(enumClass, field -> field.getAnnotation(EnumValue.class) != null);
        if (enumValueField != null) {
            hasEnumValueAnnotation = true;
        }

        if (hasEnumValueAnnotation) {
            String getterMethodName = "get" + StringUtil.firstCharToUpperCase(enumValueField.getName());

            Method getter = ClassUtil.getFirstMethod(enumClass, method -> {
                String methodName = method.getName();
                return methodName.equals(getterMethodName) && Modifier.isPublic(method.getModifiers());
            });

            propertyType = ClassUtil.getWrapType(enumValueField.getType());

            if (getter == null) {
                if (Modifier.isPublic(enumValueField.getModifiers())) {
                    property = enumValueField;
                } else {
                    throw new IllegalStateException("Can not find method \"" + getterMethodName + "()\" in enum: " + enumClass.getName());
                }
            } else {
                this.getterMethod = getter;
            }
        }

        if (!hasEnumValueAnnotation) {
            Method enumValueMethod = ClassUtil.getFirstMethod(enumClass, method -> method.getAnnotation(EnumValue.class) != null);
            if (enumValueMethod != null) {
                String methodName = enumValueMethod.getName();
                if (!(methodName.startsWith("get") && methodName.length() > 3)) {
                    throw new IllegalStateException("Can not find get method \"" + methodName + "()\" in enum: " + enumClass.getName());
                }
                this.getterMethod = enumValueMethod;
                this.hasEnumValueAnnotation = true;
            }
        }
    }


    public Object getEnumValue(E object) {
        try {
            return getterMethod != null ? getterMethod.invoke(object) : property.get(object);
        } catch (Exception e) {
            throw OrmExceptions.wrap(e);
        }
    }


    public E getEnum(Object value) {
        if (value != null) {
            for (E e : enums) {
                if (value.equals(getEnumValue(e))) {
                    return e;
                }
            }
        }
        return null;
    }

    public boolean hasEnumValueAnnotation() {
        return hasEnumValueAnnotation;
    }
}
