package cn.com.idmy.orm.core.util;

import org.dromara.hutool.core.text.StrUtil;

import java.util.regex.Pattern;

public class SqlUtil {
    private SqlUtil() {
    }

    public static void checkField(String field) {
        if (StrUtil.isBlank(field)) {
            throw new IllegalArgumentException("字段名不能为空");
        } else {
            field = field.trim();
        }
        int len = field.length();
        for (int i = 0; i < len; ++i) {
            char ch = field.charAt(i);
            if (Character.isWhitespace(ch)) {
                throw new IllegalArgumentException("字段名不能包含空字符");
            }
            if (isUnsafeChar(ch)) {
                throw new IllegalArgumentException("字段名含非法字符：[" + ch + "]");
            }
        }
    }

    /**
     * 仅支持字母、数字、下划线、空格、逗号、小数点（支持多个字段排序）
     */
    private static final Pattern ORDER_BY_PATTERN = Pattern.compile("[a-zA-Z0-9_ ,.]+");

    public static void checkOrderBy(String value) {
        if (!ORDER_BY_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("非法排序字段: " + value);
        }
    }

    private static final char[] UN_SAFE_CHARS = "'`\"<>&+=#-;".toCharArray();

    private static boolean isUnsafeChar(char ch) {
        for (int i = 0, len = UN_SAFE_CHARS.length; i < len; i++) {
            char c = UN_SAFE_CHARS[i];
            if (c == ch) {
                return true;
            }
        }
        return false;
    }

    public static boolean toBoolean(int result) {
        return result > 0 || result == -2;
    }

    public static boolean toBoolean(long result) {
        return result > 0;
    }

    public static boolean toBoolean(int[] results) {
        for (int result : results) {
            if (toBoolean(result)) {
                return true;
            }
        }
        return false;
    }
}