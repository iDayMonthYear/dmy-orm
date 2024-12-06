package cn.com.idmy.orm.util;

import cn.com.idmy.orm.core.ColumnGetter;
import org.apache.ibatis.util.MapUtil;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LambdaUtil {
    private static final Map<Class<?>, String> fieldNameMap = new ConcurrentHashMap<>();

    public static <IN, OUT> String getFieldName(ColumnGetter<IN, OUT> getter) {
        return MapUtil.computeIfAbsent(fieldNameMap, getter.getClass(), aClass -> {
            SerializedLambda lambda = getSerializedLambda(getter);
            if (lambda.getCapturedArgCount() == 1) {
                Object capturedArg = lambda.getCapturedArg(0);
                try {
                    return (String) capturedArg.getClass().getMethod("getName").invoke(capturedArg);
                } catch (Exception ignored) {
                }
            }
            return methodToFieldName(lambda.getImplMethodName());
        });
    }

    public static SerializedLambda getSerializedLambda(Serializable getter) {
        try {
            Method method = getter.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            return (SerializedLambda) method.invoke(getter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String methodToFieldName(String name) {
        if (name.startsWith("is")) {
            name = name.substring(2);
        } else if (name.startsWith("get") || name.startsWith("set")) {
            name = name.substring(3);
        }
        if (name.isEmpty()) {
            return name;
        } else {
            return name.substring(0, 1).toLowerCase(Locale.ENGLISH).concat(name.substring(1));
        }
    }
}
