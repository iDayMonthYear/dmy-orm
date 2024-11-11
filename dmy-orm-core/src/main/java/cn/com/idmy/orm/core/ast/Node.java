package cn.com.idmy.orm.core.ast;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

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
    static final class Cond extends Node {
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
    public static final class Field extends Node {
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

    @Getter
    @Accessors(fluent = true)
    static final class GroupBy extends Node {
        private final Field field;

        GroupBy(Field field) {
            super(Type.GROUP_BY);
            this.field = field;
        }
    }

    record Having(String expr) {
    }

    @Getter
    @Accessors(fluent = true)
    static final class OrderBy extends Node {
        private final Field field;
        private final boolean desc;

        OrderBy(Field field, boolean desc) {
            super(Type.ORDER_BY);
            this.field = field;
            this.desc = desc;
        }
    }

    @Getter
    @Accessors(fluent = true)
    static final class SelectField extends Node {
        private final Field field;

        SelectField(Field field) {
            super(Type.GROUP_BY);
            this.field = field;
        }
    }
}
