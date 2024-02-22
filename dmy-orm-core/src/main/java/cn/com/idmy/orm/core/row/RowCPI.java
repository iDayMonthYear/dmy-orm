package cn.com.idmy.orm.core.row;

import cn.com.idmy.orm.core.update.RawValue;

import java.util.Map;
import java.util.Set;

/**
 * cross package invoker
 */
public class RowCPI {

    private RowCPI() {
    }


    public static Set<String> getInsertAttrs(Row row) {
        return row.getInsertAttrs();
    }

    public static Object[] obtainModifyValues(Row row) {
        return row.obtainModifyValuesWithoutPk();
    }

    public static String[] obtainsPrimaryKeyStrings(Row row) {
        return row.obtainsPrimaryKeyStrings();
    }

    public static RowKey[] obtainsPrimaryKeys(Row row) {
        return row.obtainsPrimaryKeys();
    }

    public static Object[] obtainsPrimaryValues(Row row) {
        return row.obtainsPrimaryValues();
    }

    public static Object[] obtainUpdateValues(Row row) {
        return row.obtainUpdateValues();
    }

    public static Set<String> getModifyAttrs(Row row) {
        return row.getModifyAttrs();
    }

    public static Map<String, RawValue> getRawValueMap(Row row) {
        return row.getRawValueMap();
    }


}
