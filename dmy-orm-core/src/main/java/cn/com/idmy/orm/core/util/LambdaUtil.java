/*
 *  Copyright (c) 2022-2025, Mybatis-Flex (fuhai999@gmail.com).
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cn.com.idmy.orm.core.util;


import cn.com.idmy.orm.core.OrmException;
import cn.com.idmy.orm.core.query.ast.FieldGetter;
import org.apache.ibatis.util.MapUtil;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LambdaUtil {
    private static final Map<Class<?>, String> fieldNameMap = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Class<?>> implClassMap = new ConcurrentHashMap<>();

    public static <T, F> String fieldName(FieldGetter<T, F> getter) {
        return MapUtil.computeIfAbsent(fieldNameMap, getter.getClass(), $ -> {
            SerializedLambda lambda = getSerializedLambda(getter);
            if (lambda.getCapturedArgCount() == 1) {
                Object arg = lambda.getCapturedArg(0);
                try {
                    return (String) arg.getClass().getMethod("getName").invoke(arg);
                } catch (Exception ignored) {
                }
            }
            String methodName = lambda.getImplMethodName();
            return methodToField(methodName);
        });
    }

    public static String methodToField(String method) {
        if (method.startsWith("is")) {
            method = method.substring(2);
        } else if (method.startsWith("get") || method.startsWith("set")) {
            method = method.substring(3);
        }
        if (!method.isEmpty()) {
            method = method.substring(0, 1).toLowerCase(Locale.ENGLISH).concat(method.substring(1));
        }
        return method;
    }

    public static <T, F extends String> Class<?> getImplClass(FieldGetter<T, F> getter) {
        return MapUtil.computeIfAbsent(implClassMap, getter.getClass(), $ -> {
            SerializedLambda lambda = getSerializedLambda(getter);
            return getImplClass(lambda, getter.getClass().getClassLoader());
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

    public static Class<?> getImplClass(SerializedLambda lambda, ClassLoader classLoader) {
        String implClass = getImplClassName(lambda);
        try {
            return Class.forName(implClass.replace("/", "."), true, classLoader);
        } catch (ClassNotFoundException e) {
            throw new OrmException(e);
        }
    }

    public static String getImplClassName(SerializedLambda lambda) {
        String type = lambda.getInstantiatedMethodType();
        return type.substring(2, type.indexOf(";"));
    }
}
