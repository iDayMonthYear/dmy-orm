package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.constant.SqlOperator;
import cn.com.idmy.orm.core.util.LambdaGetter;
import cn.com.idmy.orm.core.util.LambdaUtil;

import java.util.HashMap;

/**
 * @author michael
 */
public class SqlOperators extends HashMap<String, SqlOperator> {

    private static final SqlOperators EMPTY = new SqlOperators() {
        @Override
        public SqlOperator put(String key, SqlOperator value) {
            throw new IllegalArgumentException("Can not set SqlOperator for \"empty\" SqlOperators");
        }
    };

    public static SqlOperators empty() {
        return EMPTY;
    }

    public static SqlOperators of() {
        return new SqlOperators();
    }

    public static <T> SqlOperators of(LambdaGetter<T> getter, SqlOperator operator) {
        SqlOperators map = new SqlOperators(1);
        map.put(LambdaUtil.getFieldName(getter), operator);
        return map;
    }

    public static <T> SqlOperators of(String fieldName, SqlOperator operator) {
        SqlOperators map = new SqlOperators(1);
        map.put(fieldName, operator);
        return map;
    }

    public SqlOperators() {
    }

    public SqlOperators(int initialCapacity) {
        super(initialCapacity);
    }

    public SqlOperators(SqlOperators sqlOperators) {
        this.putAll(sqlOperators);
    }


    public <T> SqlOperators set(LambdaGetter<T> getter, SqlOperator operator) {
        this.put(LambdaUtil.getFieldName(getter), operator);
        return this;
    }

    public SqlOperators set(String fieldName, SqlOperator operator) {
        this.put(fieldName, operator);
        return this;
    }

    public SqlOperators set(QueryColumn column, SqlOperator operator) {
        this.put(column.getName(), operator);
        return this;
    }

}
