package cn.com.idmy.orm.util;

import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.FieldGetter;
import lombok.NoArgsConstructor;
import org.apache.ibatis.util.MapUtil;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class LambdaUtil {
    private static final Map<Class<?>, String> fieldNameMap = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Class<?>> implClassMap = new ConcurrentHashMap<>(1);

    public static <IN, OUT> String getFieldName(FieldGetter<IN, OUT> getter) {
        return MapUtil.computeIfAbsent(fieldNameMap, getter.getClass(), $ -> {
            var lambda = getSerializedLambda(getter);
            if (lambda.getCapturedArgCount() == 1) {
                var capturedArg = lambda.getCapturedArg(0);
                try {
                    return (String) capturedArg.getClass().getMethod("getName").invoke(capturedArg);
                } catch (Exception ignored) {
                }
            }
            return methodToField(lambda.getImplMethodName());
        });
    }

    public static SerializedLambda getSerializedLambda(Serializable getter) {
        try {
            var method = getter.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            return (SerializedLambda) method.invoke(getter);
        } catch (Exception e) {
            throw new OrmException(e);
        }
    }

    public static String methodToField(String method) {
        if (method.startsWith("is")) {
            method = method.substring(2);
        } else if (method.startsWith("get") || method.startsWith("set")) {
            method = method.substring(3);
        }
        if (method.isEmpty()) {
            return method;
        } else {
            return method.substring(0, 1).toLowerCase(Locale.ENGLISH).concat(method.substring(1));
        }
    }

    public static <IN, OUT> Class<?> getImplClass(FieldGetter<IN, OUT> getter) {
        return MapUtil.computeIfAbsent(implClassMap, getter.getClass(), $ -> getImplClass(getSerializedLambda(getter), getter.getClass().getClassLoader()));
    }

    public static Class<?> getImplClass(SerializedLambda lambda, ClassLoader classLoader) {
        try {
            return Class.forName(getImplClassName(lambda).replace("/", "."), true, classLoader);
        } catch (ClassNotFoundException e) {
            throw new OrmException(e);
        }
    }

    public static String getImplClassName(SerializedLambda lambda) {
        String type = lambda.getInstantiatedMethodType();
        return type.substring(2, type.indexOf(";"));
    }
}