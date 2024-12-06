package cn.com.idmy.orm.core;

import cn.com.idmy.orm.util.LambdaUtil;
import cn.com.idmy.orm.util.SqlUtil;
import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import static cn.com.idmy.orm.core.SqlConsts.ASTERISK;
import static cn.com.idmy.orm.core.SqlFnName.COUNT;

@Data
@Accessors(fluent = true)
@RequiredArgsConstructor
public class Node {
    public enum Type {
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

    public interface Column {
        String column();
    }

    @Accessors(fluent = true)
    @Getter
    public static class Cond extends Node implements Column {
        final String column;
        final Op op;
        final Object expr;

        public Cond(ColumnGetter<?, ?> column, Op op, Object expr) {
            super(Type.COND);
            this.column = LambdaUtil.getFieldName(column);
            this.op = op;
            this.expr = expr;
        }

        public Cond(String column, Op op, Object expr) {
            super(Type.COND);
            this.column = column;
            this.op = op;
            this.expr = expr;
        }
    }

    public static class Or extends Node {
        public Or() {
            super(Type.OR);
        }
    }

    @Accessors(fluent = true)
    @Getter
    public static class Set extends Node implements Column {
        final String column;
        final Object expr;

        public Set(String column, Object expr) {
            super(Type.SET);
            this.column = column;
            this.expr = expr;
        }

        public Set(ColumnGetter<?, ?> column, Object expr) {
            this(LambdaUtil.getFieldName(column), expr);
        }
    }

    @Accessors(fluent = true)
    @Getter
    public static class GroupBy extends Node implements Column {
        final String column;

        public GroupBy(String column) {
            super(Type.GROUP_BY);
            this.column = column;
        }

        public GroupBy(ColumnGetter<?, ?> column) {
            this(LambdaUtil.getFieldName(column));
        }
    }

    @Accessors(fluent = true)
    @Getter
    public static class OrderBy extends Node implements Column {
        final String column;
        final boolean desc;

        public OrderBy(String column, boolean desc) {
            super(Type.ORDER_BY);
            this.column = SqlUtil.checkColumn(column);
            this.desc = desc;
        }

        public OrderBy(ColumnGetter<?, ?> column, boolean desc) {
            this(LambdaUtil.getFieldName(column), desc);
        }
    }

    @Accessors(fluent = true)
    @Getter
    public static class SelectColumn extends Node implements Column {
        String column;
        @Nullable
        SqlFnExpr<?> expr;

        public SelectColumn(String column) {
            super(Type.SELECT_COLUMN);
            this.column = SqlUtil.checkColumn(column);
        }

        public SelectColumn(ColumnGetter<?, ?> column) {
            this(LambdaUtil.getFieldName(column));
        }

        public SelectColumn(SqlFnExpr<?> expr) {
            super(Type.SELECT_COLUMN);
            this.expr = expr;
            var fn = expr.apply();
            var name = fn.name();
            if (name == COUNT && fn.column() == null) {
                column = ASTERISK;
            } else {
                column = fn.column();
            }
        }

        public SelectColumn(SqlFnExpr<?> expr, ColumnGetter<?, ?> alias) {
            this(expr);
            column = LambdaUtil.getFieldName(alias);
        }
    }

    @Accessors(fluent = true)
    @Getter
    public static class Distinct extends Node implements Column {
        @Nullable
        String column;

        public Distinct() {
            super(Type.DISTINCT);
        }

        public Distinct(@Nullable ColumnGetter<?, ?> column) {
            this();
            if (column != null) {
                this.column = LambdaUtil.getFieldName(column);
            }
        }
    }
}
