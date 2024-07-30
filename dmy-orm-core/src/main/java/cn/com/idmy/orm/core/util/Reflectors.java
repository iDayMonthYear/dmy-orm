package cn.com.idmy.orm.core.util;

import org.apache.ibatis.reflection.Reflector;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Reflectors {

    private static final ConcurrentMap<Class<?>, Reflector> reflectorMap = new ConcurrentHashMap<>();

    public static Reflector of(Class<?> type) {
        return MapUtil.computeIfAbsent(reflectorMap, type, Reflector::new);
    }

}
