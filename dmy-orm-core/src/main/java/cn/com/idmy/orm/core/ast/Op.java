package cn.com.idmy.orm.core.ast;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Op {
    EQ("="),            // Equal
    NE("<>"),          // Not Equal
    LT("<"),           // Less Than
    LE("<="),          // Less Than or Equal
    GT(">"),           // Greater Than
    GE(">="),          // Greater Than or Equal
    IN("in"),          // In
    NOT_IN("not in"),  // Not In
    BETWEEN("between"), // Between
    LIKE("like"),      // Like
    IS_NULL("is null"), // Is Null
    IS_NOT_NULL("is not null"); // Is Not Null
    ;
    private final String symbol;
}