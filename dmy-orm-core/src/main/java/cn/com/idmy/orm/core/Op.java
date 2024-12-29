package cn.com.idmy.orm.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Op {
    EQ("="),
    NE("<>"),
    LT("<"),
    LE("<="),
    GT(">"),
    GE(">="),
    IN("in"),
    NOT_IN("not in"),
    BETWEEN("between"),
    NOT_BETWEEN("not between"),
    LIKE("like"),
    NOT_LIKE("not like"),
    IS_NULL("is null"),
    IS_NOT_NULL("is not null");
    private final String symbol;
}