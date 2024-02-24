package cn.com.idmy.orm.core.util;

import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.util.MapUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Reflectors {
    private static final ConcurrentMap<Class<?>, Reflector> reflector = new ConcurrentHashMap<>();
    public static Reflector of(Class<?> type) {
        return MapUtil.computeIfAbsent(reflector, type, Reflector::new);
    }
}
