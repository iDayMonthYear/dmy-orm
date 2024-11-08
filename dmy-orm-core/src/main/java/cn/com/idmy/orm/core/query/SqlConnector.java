package cn.com.idmy.orm.core.query;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SqlConnector {
    AND(" AND "),
    OR(" OR "),
    ;
    private final String value;
}