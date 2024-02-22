package cn.com.idmy.orm.core.tenant;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TenantManager {
    private static final ThreadLocal<Boolean> ignore = new ThreadLocal<>();

    @Getter
    @Setter
    private static TenantFactory tenantFactory;

    /**
     * 忽略 tenant 条件
     */
    public static <T> T ignore(Supplier<T> supplier) {
        try {
            start();
            return supplier.get();
        } finally {
            restore();
        }
    }

    /**
     * 忽略 tenant 条件
     */
    public static void ignore(Runnable runnable) {
        try {
            start();
            runnable.run();
        } finally {
            restore();
        }
    }


    /**
     * 忽略 tenant 条件
     */
    public static void start() {
        ignore.set(Boolean.TRUE);
    }


    /**
     * 恢复 tenant 条件
     */
    public static void restore() {
        ignore.remove();
    }

    @Nullable
    public static Object[] getTenantIds() {
        Boolean bol = ignore.get();
        if (bol != null && bol) {
            return null;
        } else {
            return tenantFactory != null ? tenantFactory.getTenantIds() : null;
        }
    }
}
