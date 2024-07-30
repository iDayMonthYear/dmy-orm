package cn.com.idmy.orm.core.constant;

/**
 * SQL 操作符。
 *
 * @author michael
 * @author 王帅
 */
public enum SqlOperator {

    /**
     * ignore
     */
    IGNORE(""),

    /**
     * >
     */
    GT(SqlConsts.GT),

    /**
     * >=
     */
    GE(SqlConsts.GE),

    /**
     * <
     */
    LT(SqlConsts.LT),

    /**
     * <=
     */
    LE(SqlConsts.LE),

    /**
     * like %value%
     */
    LIKE(SqlConsts.LIKE),

    /**
     * like value%
     */
    LIKE_LEFT(SqlConsts.LIKE),

    /**
     * like %value
     */
    LIKE_RIGHT(SqlConsts.LIKE),

    /**
     * not like %value%
     */
    NOT_LIKE(SqlConsts.NOT_LIKE),

    /**
     * not like value%
     */
    NOT_LIKE_LEFT(SqlConsts.NOT_LIKE),

    /**
     * not like %value
     */
    NOT_LIKE_RIGHT(SqlConsts.NOT_LIKE),

    /**
     * =
     */
    EQUALS(SqlConsts.EQUALS),

    /**
     * !=
     */
    NOT_EQUALS(SqlConsts.NOT_EQUALS),

    /**
     * is null
     */
    IS_NULL(SqlConsts.IS_NULL),

    /**
     * is not null
     */
    IS_NOT_NULL(SqlConsts.IS_NOT_NULL),

    /**
     * in
     */
    IN(SqlConsts.IN),

    /**
     * not in
     */
    NOT_IN(SqlConsts.NOT_IN),

    /**
     * between
     */
    BETWEEN(SqlConsts.BETWEEN),

    /**
     * not between
     */
    NOT_BETWEEN(SqlConsts.NOT_BETWEEN);

    private final String value;

    SqlOperator(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
