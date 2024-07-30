package cn.com.idmy.orm.core.util;

import cn.com.idmy.orm.core.query.CloneSupport;
import jakarta.annotation.Nullable;

import java.util.Objects;

public class ObjectUtil {

    private ObjectUtil() {
    }

    public static Object cloneObject(Object value) {
        // ROLE.ROLE_ID.ge(USER.USER_ID)
        if (value instanceof CloneSupport) {
            return ((CloneSupport<?>) value).clone();
        }
        return value;
    }

    @Nullable
    public static <T extends CloneSupport<T>> T clone(T value) {
        if (value != null) {
            return value.clone();
        }
        return null;
    }

    public static <T> T requireNonNullElse(T t1, T t2) {
        return t1 == null ? t2 : t1;
    }

    public static boolean areNotNull(Object... objs) {
        for (Object obj : objs) {
            if (obj == null) {
                return false;
            }
        }
        return true;
    }

    public static boolean areNull(Object... objs) {
        for (Object obj : objs) {
            if (obj != null) {
                return false;
            }
        }
        return true;
    }

    public static boolean equalsAny(Object a, Object... others) {
        if (others == null || others.length == 0) {
            throw new IllegalArgumentException("others must not be null or empty.");
        }
        for (Object other : others) {
            if (Objects.equals(a, other)) {
                return true;
            }
        }
        return false;
    }

}
