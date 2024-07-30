package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.util.StringUtil;

import java.util.Collection;
import java.util.Map;

public class If {

    private If() {
    }

    /**
     * 判断对象是否为空
     */
    public static boolean isNull(Object object) {
        return object == null;
    }

    /**
     * 判断对象是否非空
     */
    public static boolean notNull(Object object) {
        return object != null;
    }

    public static <T> boolean isEmpty(T[] array) {
        return array != null && array.length == 0;
    }

    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map != null && map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection != null && collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 查看某个 string 对象是否有文本内容
     */
    public static boolean hasText(String string) {
        return StringUtil.isNotBlank(string);
    }

}
