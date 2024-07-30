package cn.com.idmy.orm.core.tenant;

import jakarta.annotation.Nullable;
import lombok.Getter;

import java.util.function.Supplier;

public class TenantManager {

    private TenantManager() {
    }

    private static final ThreadLocal<Boolean> ignoreFlags = new ThreadLocal<>();

    @Getter
    private static TenantFactory tenantFactory;

    public static void setTenantFactory(TenantFactory tenantFactory) {
        TenantManager.tenantFactory = tenantFactory;
    }

    /**
     * 忽略 tenant 条件
     */
    public static <T> T withoutTenantCondition(Supplier<T> supplier) {
        try {
            ignoreTenantCondition();
            return supplier.get();
        } finally {
            restoreTenantCondition();
        }
    }

    /**
     * 忽略 tenant 条件
     */
    public static void withoutTenantCondition(Runnable runnable) {
        try {
            ignoreTenantCondition();
            runnable.run();
        } finally {
            restoreTenantCondition();
        }
    }


    /**
     * 忽略 tenant 条件
     */
    public static void ignoreTenantCondition() {
        ignoreFlags.set(Boolean.TRUE);
    }


    /**
     * 恢复 tenant 条件
     */
    public static void restoreTenantCondition() {
        ignoreFlags.remove();
    }

    /**
     * @deprecated 使用 {@link #getTenantIds(String)} 代替。
     */
    @Deprecated
    public static Object[] getTenantIds() {
        return getTenantIds(null);
    }

    @Nullable
    public static Object[] getTenantIds(String tableName) {
        Boolean ignoreFlag = ignoreFlags.get();
        if (ignoreFlag != null && ignoreFlag) {
            return null;
        }
        return tenantFactory != null ? tenantFactory.getTenantIds(tableName) : null;
    }
}
