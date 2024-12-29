package cn.com.idmy.orm.core;

import cn.com.idmy.base.util.SqlUtil;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @NotNull
    final SqlNodeType type;

    public interface SqlColumn {
        @NotNull
        String column();
    }

    @Getter
    @Accessors(fluent = true)
    public static class SqlCond extends SqlNode implements SqlColumn {
        @NotNull
        final String column;
        @NotNull
        final Op op;
        @NotNull
        final Object expr;

        public SqlCond(@NotNull String column, @NonNull Op op, @NotNull Object expr) {
            super(SqlNodeType.COND);
            this.column = SqlUtil.checkColumn(column);
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
        @NotNull
        final String column;
        @Nullable
        final Object expr;

        public SqlSet(@NotNull String column, @Nullable Object expr) {
            super(SqlNodeType.SET);
            this.column = SqlUtil.checkColumn(column);
            this.expr = expr;
        }

        public SqlSet(@NotNull String column, @NonNull SqlOpExpr expr) {
            super(SqlNodeType.SET);
            this.column = SqlUtil.checkColumn(column);
            this.expr = expr;
        }
    }

    @Getter
    @Accessors(fluent = true)
    public static class SqlGroupBy extends SqlNode implements SqlColumn {
        @NotNull
        final String column;

        public SqlGroupBy(@NotNull String column) {
            super(SqlNodeType.GROUP_BY);
            this.column = SqlUtil.checkColumn(column);
        }
    }

    @Getter
    @Accessors(fluent = true)
    public static class SqlOrderBy extends SqlNode implements SqlColumn {
        @NotNull
        final String column;
        final boolean desc;

        public SqlOrderBy(@NotNull String column, boolean desc) {
            super(SqlNodeType.ORDER_BY);
            this.column = SqlUtil.checkColumn(column);
            this.desc = desc;
        }
    }

    @Getter
    @Accessors(fluent = true)
    public static class SqlSelectColumn extends SqlNode implements SqlColumn {
        @NotNull
        String column;
        @Nullable
        SqlFnExpr<?> expr;

        public SqlSelectColumn(@NotNull String column) {
            super(SqlNodeType.SELECT_COLUMN);
            this.column = SqlUtil.checkColumn(column);
        }

        public SqlSelectColumn(@NonNull SqlFnExpr<?> expr) {
            super(SqlNodeType.SELECT_COLUMN);
            this.expr = expr;
            var fn = expr.apply(new SqlFn<>());
            var name = fn.name();
            column = name == COUNT ? ASTERISK : fn.column();
        }

        public SqlSelectColumn(@NonNull SqlFnExpr<?> expr, @NotNull String alias) {
            this(expr);
            column = SqlUtil.checkColumn(alias);
        }
    }

    @Getter
    @Accessors(fluent = true)
    public static class SqlDistinct extends SqlNode implements SqlColumn {
        @NotNull
        String column;

        public SqlDistinct() {
            super(SqlNodeType.DISTINCT);
            this.column = "";
        }

        public SqlDistinct(@NotNull String column) {
            this();
            this.column = SqlUtil.checkColumn(column);
        }
    }
}
