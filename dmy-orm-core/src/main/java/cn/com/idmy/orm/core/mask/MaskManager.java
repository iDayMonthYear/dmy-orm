package cn.com.idmy.orm.core.mask;

import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class MaskManager {
    private static final Map<String, MaskProcessor> processors = new HashMap<>();
    private static final ThreadLocal<Boolean> mask = new ThreadLocal<>();

    static {
        register(Masks.MOBILE, Masks.MOBILE_PROCESSOR);
        register(Masks.FIXED_PHONE, Masks.FIXED_PHONE_PROCESSOR);
        register(Masks.ID_CARD_NO, Masks.ID_CARD_NO_PROCESSOR);
        register(Masks.CHINESE_NAME, Masks.CHINESE_NAME_PROCESSOR);
        register(Masks.ADDRESS, Masks.ADDRESS_PROCESSOR);
        register(Masks.EMAIL, Masks.EMAIL_PROCESSOR);
        register(Masks.PASSWORD, Masks.PASSWORD_PROCESSOR);
        register(Masks.CAR_LICENSE, Masks.CAR_LICENSE_PROCESSOR);
        register(Masks.BANK_CARD_NO, Masks.BANK_CARD_NO_PROCESSOR);
    }

    public static void register(String type, MaskProcessor processor) {
        processors.put(type, processor);
    }

    public static Map<String, MaskProcessor> findProcessors() {
        return Collections.unmodifiableMap(processors);
    }

    public static <T> T mask(Supplier<T> supplier) {
        try {
            start();
            return supplier.get();
        } finally {
            stop();
        }
    }

    public static void mask(Runnable runnable) {
        try {
            start();
            runnable.run();
        } finally {
            stop();
        }
    }

    public static void start() {
        mask.set(Boolean.TRUE);
    }

    public static void stop() {
        mask.remove();
    }

    public static Object mask(String type, Object data) {
        if (Objects.equals(MaskManager.mask.get(), Boolean.TRUE)) {
            MaskProcessor processor = processors.get(type);
            if (processor == null) {
                throw new IllegalStateException("Can not get mask processor for by type: " + type);
            } else {
                return processor.mask(data);
            }
        } else {
            return data;
        }
    }

}
