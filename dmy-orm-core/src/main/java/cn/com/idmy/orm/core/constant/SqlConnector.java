package cn.com.idmy.orm.core.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author michael
 */
@Getter
@RequiredArgsConstructor
public enum SqlConnector {
    /**
     * And
     */
    AND(" AND "),

    /**
     * OR
     */
    OR(" OR "),
    ;

    private final String value;

    @Override
    public String toString() {
        return value;
    }
}
