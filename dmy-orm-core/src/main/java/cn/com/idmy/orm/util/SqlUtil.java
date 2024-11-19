package cn.com.idmy.orm.util;

import java.util.regex.Pattern;

public class SqlUtil {
    private static final Pattern FIELD_PATTERN = Pattern.compile("[a-zA-Z0-9_]+");

    public static void checkField(String field) {
        if (!FIELD_PATTERN.matcher(field).matches()) {
            throw new IllegalArgumentException("非法字段名：" + field);
        }
    }
}