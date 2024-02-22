package cn.com.idmy.orm.core.util;

import cn.com.idmy.orm.core.exception.OrmExceptions;
import cn.com.idmy.orm.core.query.QueryColumn;
import cn.com.idmy.orm.core.table.TableInfo;
import cn.com.idmy.orm.core.table.TableInfoFactory;
import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.apache.ibatis.util.MapUtil;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LambdaUtil {
    private static final Map<Class<?>, SerializedLambda> lambdas = new ConcurrentHashMap<>();
    private static final Map<String, Class<?>> classes = new ConcurrentHashMap<>();

    public static <T> String getFieldName(LambdaGetter<T> getter) {
        SerializedLambda lambda = getSerializedLambda(getter);
        String methodName = lambda.getImplMethodName();
        return PropertyNamer.methodToProperty(methodName);
    }

    public static <T> Class<?> getImplClass(LambdaGetter<T> getter) {
        SerializedLambda lambda = getSerializedLambda(getter);
        return getImplClass(lambda, getter.getClass().getClassLoader());
    }

    public static <T> String getAliasName(LambdaGetter<T> getter, boolean withPrefix) {
        QueryColumn queryColumn = getQueryColumn(getter);
        if (queryColumn != null) {
            String alias = StrUtil.isNotBlank(queryColumn.getAlias()) ? queryColumn.getAlias() : queryColumn.getName();
            return withPrefix ? queryColumn.getTable().getName() + "$" + alias : alias;
        }
        return getFieldName(getter);
    }


    public static <T> QueryColumn getQueryColumn(LambdaGetter<T> getter) {
        ClassLoader classLoader = getter.getClass().getClassLoader();
        SerializedLambda lambda = getSerializedLambda(getter);
        String methodName = lambda.getImplMethodName();
        Class<?> entityClass = getImplClass(lambda, classLoader);
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(entityClass);
        return tableInfo.getQueryColumnByProperty(PropertyNamer.methodToProperty(methodName));
    }


    public static SerializedLambda getSerializedLambda(Serializable getter) {
        return MapUtil.computeIfAbsent(lambdas, getter.getClass(), aClass -> {
            try {
                Method method = getter.getClass().getDeclaredMethod("writeReplace");
                method.setAccessible(Boolean.TRUE);
                return (SerializedLambda) method.invoke(getter);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static Class<?> getImplClass(SerializedLambda lambda, ClassLoader classLoader) {
        String implClass = getImplClassName(lambda);
        return MapUtil.computeIfAbsent(classes, implClass, s -> {
            try {
                return Class.forName(s.replace("/", "."), true, classLoader);
            } catch (ClassNotFoundException e) {
                throw OrmExceptions.wrap(e);
            }
        });
    }

    private static String getImplClassName(SerializedLambda lambda) {
        String type = lambda.getInstantiatedMethodType();
        return type.substring(2, type.indexOf(";"));
    }
}
