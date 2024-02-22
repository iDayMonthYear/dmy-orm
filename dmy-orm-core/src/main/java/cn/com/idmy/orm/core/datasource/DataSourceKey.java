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
        try {
            use(dataSourceKey);
            return supplier.get();
        } finally {
            clear();
        }
    }

    public static void use(String dataSourceKey, Runnable runnable) {
        try {
            use(dataSourceKey);
            runnable.run();
        } finally {
            clear();
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

    public static String getByShardingStrategy(String dataSource, Object mapper, Method method, Object[] args) {
        String shardingDsKey = DataSourceManager.getByShardingStrategy(dataSource, mapper, method, args);
        return shardingDsKey != null ? shardingDsKey : dataSource;
    }
}
