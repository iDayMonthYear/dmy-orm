package cn.com.idmy.orm.core;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
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
        SELECT_COLUMN,
        DISTINCT,
        LIMIT,
        OFFSET;
    }

    private final Type type;

    @Getter
    @Accessors(fluent = true)
    static final class Cond extends Node {
        private final Object column;
        private final Op op;
        private final Object expr;

        Cond(ColumnGetter<?, ?> column, Op op, Object expr) {
            super(Type.COND);
            this.column = column;
            this.op = op;
            this.expr = expr;
        }

        Cond(String column, Op op, Object expr) {
            super(Type.COND);
            this.column = column;
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
        private final ColumnGetter<?, ?> column;
        private final Object expr;

        Set(ColumnGetter<?, ?> column, Object expr) {
            super(Type.SET);
            this.column = column;
            this.expr = expr;
        }
    }

    @Getter
    @Accessors(fluent = true)
    static final class GroupBy extends Node {
        private final ColumnGetter<?, ?> column;

        GroupBy(ColumnGetter<?, ?> column) {
            super(Type.GROUP_BY);
            this.column = column;
        }
    }

    @Getter
    @Accessors(fluent = true)
    static final class OrderBy extends Node {
        private final Object column;
        private final boolean desc;

        OrderBy(ColumnGetter<?, ?> column, boolean desc) {
            super(Type.ORDER_BY);
            this.column = column;
            this.desc = desc;
        }

        OrderBy(String column, boolean desc) {
            super(Type.ORDER_BY);
            this.column = column;
            this.desc = desc;
        }
    }

    @Getter
    @Accessors(fluent = true)
    static final class SelectColumn extends Node {
        private final Object column; //ColumnGetter<?, ?> | SqlFnExpr
        @Nullable
        private ColumnGetter<?, ?> alias;

        SelectColumn(ColumnGetter<?, ?> column) {
            super(Type.SELECT_COLUMN);
            this.column = column;
        }

        SelectColumn(String column) {
            super(Type.SELECT_COLUMN);
            this.column = column;
        }

        SelectColumn(SqlFnExpr<?> expr) {
            super(Type.SELECT_COLUMN);
            this.column = expr;
        }

        SelectColumn(SqlFnExpr<?> expr, ColumnGetter<?, ?> alias) {
            super(Type.SELECT_COLUMN);
            this.column = expr;
            this.alias = alias;
        }
    }

    @Getter
    @Accessors(fluent = true)
    static final class Distinct extends Node {
        @Nullable
        private ColumnGetter<?, ?> column;

        Distinct() {
            super(Type.DISTINCT);
        }

        Distinct(@Nullable ColumnGetter<?, ?> column) {
            this();
            this.column = column;
        }
    }
}
