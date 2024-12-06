package cn.com.idmy.orm.util;

import cn.com.idmy.orm.OrmException;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class SqlUtil {
    private static final Pattern FIELD_PATTERN = Pattern.compile("[a-zA-Z0-9_]+");

    public static String checkColumn(String col) {
        if (FIELD_PATTERN.matcher(col).matches()) {
            return col;
        } else {
            throw new OrmException("非法列名：" + col);
        }
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