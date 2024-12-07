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

    public static boolean toBoolean(int val) {
        return val > 0 || val == -2;
    }

    public static boolean toBoolean(long val) {
        return val > 0;
    }

    public static boolean toBoolean(int[] vals) {
        for (int val : vals) {
            if (toBoolean(val)) {
                return true;
            }
        }
        return false;
    }
}