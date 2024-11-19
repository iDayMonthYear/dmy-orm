package cn.com.idmy.orm.ast;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SqlFnName {
    COUNT("count"),
    SUM("sum"),
    MAX("max"),
    MIN("min"),
    AVG("avg"),
    ABS("abs"),
    LENGTH("length"),
    IF_NULL("ifnull"),
    ;
    private final String name;
}
