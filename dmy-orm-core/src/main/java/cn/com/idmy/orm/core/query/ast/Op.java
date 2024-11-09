package cn.com.idmy.orm.core.query.ast;

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
    IN("IN"),          // In
    NOT_IN("NOT IN"),  // Not In
    BETWEEN("BETWEEN"), // Between
    LIKE("LIKE"),      // Like
    IS_NULL("IS NULL"), // Is Null
    IS_NOT_NULL("IS NOT NULL"); // Is Not Null
    ;
    private final String symbol;
}