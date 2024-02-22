package cn.com.idmy.orm.core.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author michael
 */
@Getter
@RequiredArgsConstructor
public enum SqlOperator {

    // >
    GT(SqlConsts.GT),

    // >=
    GE(SqlConsts.GE),

    // <
    LT(SqlConsts.LT),

    // <=
    LE(SqlConsts.LE),

    // like
    LIKE(SqlConsts.LIKE),

    // not like
    NOT_LIKE(SqlConsts.NOT_LIKE),

    // =
    EQUALS(SqlConsts.EQUALS),

    // !=
    NOT_EQUALS(SqlConsts.NOT_EQUALS),

    // is null
    IS_NULL(SqlConsts.IS_NULL),

    // is not null
    IS_NOT_NULL(SqlConsts.IS_NOT_NULL),

    // in
    IN(SqlConsts.IN),

    // not in
    NOT_IN(SqlConsts.NOT_IN),

    // between
    BETWEEN(SqlConsts.BETWEEN),

    // not between
    NOT_BETWEEN(SqlConsts.NOT_BETWEEN),
    ;

    private final String value;
}
