package cn.com.idmy.orm.core.ast;

import jakarta.annotation.Nullable;
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
        WHERE,
        ORDER_BY,
        GROUP_BY,
        SET,
        OR,
        AND,
        SELECT_FIELD,
        DISTINCT,
        LIMIT,
        OFFSET;
    }

    private final Type type;

    @Getter
    @Accessors(fluent = true)
    static final class Cond extends Node {
        private final FieldGetter<?, ?> field;
        private final Op op;
        private final Object expr;

        Cond(FieldGetter<?, ?> field, Op op, Object expr) {
            super(Type.COND);
            this.field = field;
            this.op = op;
            this.expr = expr;
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
        private final FieldGetter<?, ?> field;
        private final Object expr;

        Set(FieldGetter<?, ?> field, Object expr) {
            super(Type.SET);
            this.field = field;
            this.expr = expr;
        }
    }

    @Getter
    @Accessors(fluent = true)
    static final class GroupBy extends Node {
        private final FieldGetter<?, ?> field;

        GroupBy(FieldGetter<?, ?> field) {
            super(Type.GROUP_BY);
            this.field = field;
        }
    }

    @Getter
    @Accessors(fluent = true)
    static final class OrderBy extends Node {
        private final FieldGetter<?, ?> field;
        private final boolean desc;

        OrderBy(FieldGetter<?, ?> field, boolean desc) {
            super(Type.ORDER_BY);
            this.field = field;
            this.desc = desc;
        }
    }

    @Getter
    @Accessors(fluent = true)
    static final class SelectField extends Node {
        private final Object field; //FieldGetter<?, ?> | SqlFnExpr
        private FieldGetter<?, ?> alias;

        SelectField(FieldGetter<?, ?> field) {
            super(Type.SELECT_FIELD);
            this.field = field;
        }

        SelectField(SqlFnExpr<?> expr) {
            super(Type.SELECT_FIELD);
            this.field = expr;
        }

        SelectField(SqlFnExpr<?> expr, FieldGetter<?, ?> alias) {
            super(Type.SELECT_FIELD);
            this.field = expr;
            this.alias = alias;
        }
    }

    @Getter
    @Accessors(fluent = true)
    static final class Distinct extends Node {
        @Nullable
        private FieldGetter<?, ?> field;

        Distinct() {
            super(Type.DISTINCT);
        }

        Distinct( @Nullable FieldGetter<?, ?> field) {
            this();
            this.field = field;
        }
    }
}
