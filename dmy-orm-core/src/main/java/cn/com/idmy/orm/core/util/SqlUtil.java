package cn.com.idmy.orm.core.util;

import java.util.Collection;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SqlUtil {
    private static final Pattern FIELD_PATTERN = Pattern.compile("[a-zA-Z0-9_]+");

    public static void checkField(String field) {
        if (!FIELD_PATTERN.matcher(field).matches()) {
            throw new IllegalArgumentException("非法字段名：" + field);
        }
    }

    public static String escapeSql(String value) {
        if (value == null) {
            return null;
        }
        return value.replace("'", "''")
                   .replace("\\", "\\\\");
    }

    public static String buildInClause(Collection<?> values) {
        if (values == null || values.isEmpty()) {
            return "()";
        }
        return values.stream()
                    .map(SqlUtil::formatValue)
                    .collect(Collectors.joining(",", "(", ")"));
    }

    public static String formatValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return "'" + escapeSql((String) value) + "'";
        }
        return value.toString();
    }
}