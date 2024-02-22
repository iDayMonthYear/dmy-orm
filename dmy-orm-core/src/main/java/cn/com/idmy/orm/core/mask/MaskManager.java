package cn.com.idmy.orm.core.mask;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 数据脱敏工厂类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaskManager {
    private static final Map<String, MaskProcessor> processors = new HashMap<>();

    private static final ThreadLocal<Boolean> ignore = new ThreadLocal<>();

    static {
        register(Masks.MOBILE, Masks.MOBILE_PROCESSOR);
        register(Masks.ID_CARD_NO, Masks.ID_CARD_NO_PROCESSOR);
        register(Masks.PASSWORD, Masks.PASSWORD_PROCESSOR);
    }

    public static void register(String type, MaskProcessor processor) {
        processors.put(type, processor);
    }

    public static <T> T ignore(Supplier<T> supplier) {
        try {
            start();
            return supplier.get();
        } finally {
            restore();
        }
    }

    public static void ignore(Runnable runnable) {
        try {
            start();
            runnable.run();
        } finally {
            restore();
        }
    }

    public static void start() {
        ignore.set(Boolean.TRUE);
    }

    public static void restore() {
        ignore.remove();
    }

    public static Object mask(String type, Object data) {
        Boolean bol = ignore.get();
        if (bol != null && bol) {
            return data;
        }

        MaskProcessor processor = processors.get(type);
        if (processor == null) {
            throw new IllegalStateException("Can not get mask processor for by type: " + type);
        } else {
            return processor.mask(data);
        }
    }
}
