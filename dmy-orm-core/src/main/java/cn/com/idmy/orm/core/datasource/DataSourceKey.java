package cn.com.idmy.orm.core.datasource;

import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * @author michael
 */
public class DataSourceKey {

    /**
     * 通过注解设置的 key
     */
    private static ThreadLocal<String> annotationKeyThreadLocal = new ThreadLocal<>();

    /**
     * 通过手动编码指定的 key
     */
    private static ThreadLocal<String> manualKeyThreadLocal = new ThreadLocal<>();

    private DataSourceKey() {
    }

    public static void use(String dataSourceKey) {
        manualKeyThreadLocal.set(dataSourceKey.trim());
    }

    public static void useWithAnnotation(String dataSourceKey) {
        annotationKeyThreadLocal.set(dataSourceKey.trim());
    }

    public static <T> T use(String dataSourceKey, Supplier<T> supplier) {
        String prevKey = manualKeyThreadLocal.get();
        try {
            manualKeyThreadLocal.set(dataSourceKey);
            return supplier.get();
        } finally {
            if (prevKey != null) {
                manualKeyThreadLocal.set(prevKey);
            } else {
                clear();
            }
        }
    }

    public static void use(String dataSourceKey, Runnable runnable) {
        String prevKey = manualKeyThreadLocal.get();
        try {
            manualKeyThreadLocal.set(dataSourceKey);
            runnable.run();
        } finally {
            if (prevKey != null) {
                manualKeyThreadLocal.set(prevKey);
            } else {
                clear();
            }
        }
    }

    public static void clear() {
        annotationKeyThreadLocal.remove();
        manualKeyThreadLocal.remove();
    }

    public static String getByAnnotation() {
        return annotationKeyThreadLocal.get();
    }

    public static String getByManual() {
        return manualKeyThreadLocal.get();
    }

    public static String get() {
        String key = manualKeyThreadLocal.get();
        return key != null ? key : annotationKeyThreadLocal.get();
    }

    public static void setAnnotationKeyThreadLocal(ThreadLocal<String> annotationKeyThreadLocal) {
        DataSourceKey.annotationKeyThreadLocal = annotationKeyThreadLocal;
    }

    public static void setManualKeyThreadLocal(ThreadLocal<String> manualKeyThreadLocal) {
        DataSourceKey.manualKeyThreadLocal = manualKeyThreadLocal;
    }

    public static String getShardingDsKey(String dataSource, Object mapper, Method method, Object[] args) {
        String shardingDsKey = DataSourceManager.getShardingDsKey(dataSource, mapper, method, args);
        return shardingDsKey != null ? shardingDsKey : dataSource;
    }
}
