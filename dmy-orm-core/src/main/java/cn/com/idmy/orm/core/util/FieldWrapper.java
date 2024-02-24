package cn.com.idmy.orm.core.util;

import cn.com.idmy.orm.annotation.Column;
import cn.hutool.core.util.ArrayUtil;
import lombok.Getter;
import org.apache.ibatis.reflection.Reflector;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FieldWrapper {
    public static Map<Class<?>, Map<String, FieldWrapper>> cache = new ConcurrentHashMap<>();
    @Getter
    private Field field;
    private boolean isIgnore = false;
    @Getter
    private Class<?> fieldType;
    @Getter
    private Class<?> mappingType;
    @Getter
    private Class<?> keyType;
    private Method getterMethod;
    private Method setterMethod;

    public static FieldWrapper of(Class<?> clazz, String fieldName) {
        Map<String, FieldWrapper> wrapperMap = cache.get(clazz);
        if (wrapperMap == null) {
            synchronized (clazz) {
                if (wrapperMap == null) {
                    wrapperMap = new ConcurrentHashMap<>();
                    cache.put(clazz, wrapperMap);
                }
            }
        }

        FieldWrapper fieldWrapper = wrapperMap.get(fieldName);
        if (fieldWrapper == null) {
            synchronized (clazz) {
                fieldWrapper = wrapperMap.get(fieldName);
                if (fieldWrapper == null) {
                    Field findField = ClassUtil.getFirstField(clazz, field -> field.getName().equals(fieldName));
                    if (findField == null) {
                        throw new IllegalStateException("Can not find field \"" + fieldName + "\" in class: " + clazz.getName());
                    }

                    String setterName = "set" + StringUtil.firstCharToUpperCase(fieldName);
                    Method setter = ClassUtil.getFirstMethod(clazz, method ->
                            method.getParameterCount() == 1
                                    && Modifier.isPublic(method.getModifiers())
                                    && method.getName().equals(setterName));

                    fieldWrapper = new FieldWrapper();
                    fieldWrapper.field = findField;
                    fieldWrapper.fieldType = findField.getType();
                    initMappingTypeAndKeyType(clazz, findField, fieldWrapper);

                    Column column = findField.getAnnotation(Column.class);
                    if (column != null && column.ignore()) {
                        fieldWrapper.isIgnore = true;
                    }

                    fieldWrapper.setterMethod = setter;

                    String[] getterNames = new String[]{"get" + StringUtil.firstCharToUpperCase(fieldName), "is" + StringUtil.firstCharToUpperCase(fieldName)};
                    fieldWrapper.getterMethod = ClassUtil.getFirstMethod(clazz, method -> method.getParameterCount() == 0
                            && Modifier.isPublic(method.getModifiers())
                            && ArrayUtil.contains(getterNames, method.getName()));

                    wrapperMap.put(fieldName, fieldWrapper);
                }
            }
        }

        return fieldWrapper;
    }

    private static void initMappingTypeAndKeyType(Class<?> clazz, Field field, FieldWrapper fieldWrapper) {
        Reflector reflector = Reflectors.of(clazz);
        Class<?> fieldType = reflector.getGetterType(field.getName());

        if (Collection.class.isAssignableFrom(fieldType)) {
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                Type actualTypeArgument = ((ParameterizedType) genericType).getActualTypeArguments()[0];
                fieldWrapper.mappingType = (Class<?>) actualTypeArgument;
            }
        } else if (Map.class.isAssignableFrom(fieldType)) {
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                fieldWrapper.keyType = (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
                Type actualTypeArgument = ((ParameterizedType) genericType).getActualTypeArguments()[1];
                if (actualTypeArgument instanceof ParameterizedType) {
                    fieldWrapper.mappingType = (Class<?>) ((ParameterizedType) actualTypeArgument).getRawType();
                } else {
                    fieldWrapper.mappingType = (Class<?>) actualTypeArgument;
                }
            }
        } else {
            fieldWrapper.mappingType = fieldType;
        }
    }

    public void set(Object value, Object to) {
        try {
            if (setterMethod == null) {
                throw new IllegalStateException("Can not find method \"set" + StringUtil.firstCharToUpperCase(field.getName()) + "\" in class: " + to.getClass().getName());
            }
            setterMethod.invoke(to, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object get(Object target) {
        try {
            if (getterMethod == null) {
                throw new IllegalStateException("Can not find method \"get" + StringUtil.firstCharToUpperCase(field.getName()) + ", is"
                        + StringUtil.firstCharToUpperCase(field.getName()) + "\" in class: " + target.getClass().getName());
            }
            return getterMethod.invoke(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isIgnore() {
        return isIgnore;
    }
}
