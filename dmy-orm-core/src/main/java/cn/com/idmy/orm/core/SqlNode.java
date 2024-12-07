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
public class SqlNode {
    public enum SqlNodeType {
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

    final SqlNodeType type;

    public interface SqlColumn {
        String column();
    }

    @Getter
    @Accessors(fluent = true)
    public static class SqlCond extends SqlNode implements SqlColumn {
        final String column;
        final Op op;
        final Object expr;

        public SqlCond(ColumnGetter<?, ?> column, Op op, Object expr) {
            super(SqlNodeType.COND);
            this.column = LambdaUtil.getFieldName(column);
            this.op = op;
            this.expr = expr;
        }

        public SqlCond(String column, Op op, Object expr) {
            super(SqlNodeType.COND);
            this.column = column;
            this.op = op;
            this.expr = expr;
        }
    }

    public static class SqlOr extends SqlNode {
        public SqlOr() {
            super(SqlNodeType.OR);
        }
    }

    @Getter
    @Accessors(fluent = true)
    public static class SqlSet extends SqlNode implements SqlColumn {
        final String column;
        final Object expr;

        public SqlSet(String column, Object expr) {
            super(SqlNodeType.SET);
            this.column = column;
            this.expr = expr;
        }

        public SqlSet(ColumnGetter<?, ?> column, Object expr) {
            this(LambdaUtil.getFieldName(column), expr);
        }
    }

    @Getter
    @Accessors(fluent = true)
    public static class SqlGroupBy extends SqlNode implements SqlColumn {
        final String column;

        public SqlGroupBy(String column) {
            super(SqlNodeType.GROUP_BY);
            this.column = column;
        }

        public SqlGroupBy(ColumnGetter<?, ?> column) {
            this(LambdaUtil.getFieldName(column));
        }
    }

    @Getter
    @Accessors(fluent = true)
    public static class SqlOrderBy extends SqlNode implements SqlColumn {
        final String column;
        final boolean desc;

        public SqlOrderBy(String column, boolean desc) {
            super(SqlNodeType.ORDER_BY);
            this.column = SqlUtil.checkColumn(column);
            this.desc = desc;
        }

        public SqlOrderBy(ColumnGetter<?, ?> column, boolean desc) {
            this(LambdaUtil.getFieldName(column), desc);
        }
    }

    @Getter
    @Accessors(fluent = true)
    public static class SqlSelectColumn extends SqlNode implements SqlColumn {
        String column;
        @Nullable
        SqlFnExpr<?> expr;

        public SqlSelectColumn(String column) {
            super(SqlNodeType.SELECT_COLUMN);
            this.column = SqlUtil.checkColumn(column);
        }

        public SqlSelectColumn(ColumnGetter<?, ?> column) {
            this(LambdaUtil.getFieldName(column));
        }

        public SqlSelectColumn(SqlFnExpr<?> expr) {
            super(SqlNodeType.SELECT_COLUMN);
            this.expr = expr;
            var fn = expr.apply();
            var name = fn.name();
            if (name == COUNT && fn.column() == null) {
                column = ASTERISK;
            } else {
                column = fn.column();
            }
        }

        public SqlSelectColumn(SqlFnExpr<?> expr, ColumnGetter<?, ?> alias) {
            this(expr);
            column = LambdaUtil.getFieldName(alias);
        }
    }

    @Getter
    @Accessors(fluent = true)
    public static class SqlDistinct extends SqlNode implements SqlColumn {
        @Nullable
        String column;

        public SqlDistinct() {
            super(SqlNodeType.DISTINCT);
        }

        public SqlDistinct(@Nullable ColumnGetter<?, ?> column) {
            this();
            if (column != null) {
                this.column = LambdaUtil.getFieldName(column);
            }
        }
    }
}
