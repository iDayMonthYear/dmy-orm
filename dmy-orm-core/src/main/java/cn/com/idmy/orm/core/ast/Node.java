package cn.com.idmy.orm.core.ast;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(fluent = true)
@Setter
@Getter
@RequiredArgsConstructor
public class Node {
    enum Type {
        COND,
        FIELD,
        WHERE,
        HAVING,
        ORDER_BY,
        GROUP_BY,
        SET,
        OR,
        AND,
        SELECT_FIELD,
        LIMIT,
        OFFSET;
    }

    private final Type type;
    private Object value;

    @Getter
    @Accessors(fluent = true)
    static class Cond extends Node {
        private final Field field;
        private final Op op;
        private final Object expr;

        Cond(Field field, Op op, Object expr) {
            super(Type.COND);
            this.field = field;
            this.op = op;
            this.expr = expr;
        }
    }

    @Getter
    @Accessors(fluent = true)
    public static class Field extends Node {
        private final Object name;

        public Field(String name) {
            super(Type.FIELD);
            this.name = name;
        }

        public Field(FieldGetter<?, ?> getter) {
            super(Type.FIELD);
            this.name = getter;
        }
    }

    @Getter
    static class Or extends Node {
        Or() {
            super(Type.OR);
        }
    }

    @Getter
    @Accessors(fluent = true)
    static final class Set extends Node {
        private final Field field;
        private final Object expr;

        Set(Field field, Object expr) {
            super(Type.SET);
            this.field = field;
            this.expr = expr;
        }
    }

    record GroupBy(List<Object> fields) {
    }

    record Having(String expr) {
    }

    record OrderBy(Object field, boolean desc) {
    }

    record SelectField(List<Object> fields) {

    }
}
