package cn.com.idmy.orm.core;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;

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
        DISTINCT
    }

    final Type type;

    public static final class Cond extends Node {
        final Object column;
        final Op op;
        final Object expr;

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

    static class Or extends Node {
        Or() {
            super(Type.OR);
        }
    }

    static final class Set extends Node {
        final ColumnGetter<?, ?> column;
        final Object expr;

        Set(ColumnGetter<?, ?> column, Object expr) {
            super(Type.SET);
            this.column = column;
            this.expr = expr;
        }
    }

    static final class GroupBy extends Node {
        final ColumnGetter<?, ?> column;

        GroupBy(ColumnGetter<?, ?> column) {
            super(Type.GROUP_BY);
            this.column = column;
        }
    }

    static final class OrderBy extends Node {
        final Object column;
        final boolean desc;

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

    static final class SelectColumn extends Node {
        final Object column; //ColumnGetter<?, ?> | SqlFnExpr
        @Nullable
        ColumnGetter<?, ?> alias;

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

    static final class Distinct extends Node {
        @Nullable
        ColumnGetter<?, ?> column;

        Distinct() {
            super(Type.DISTINCT);
        }

        Distinct(@Nullable ColumnGetter<?, ?> column) {
            this();
            this.column = column;
        }
    }
}
