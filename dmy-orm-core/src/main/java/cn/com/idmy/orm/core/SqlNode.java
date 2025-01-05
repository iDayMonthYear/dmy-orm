package cn.com.idmy.orm.core;

import cn.com.idmy.base.util.SqlUtil;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.TableInfo.TableColumn;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.dromara.hutool.core.collection.iter.IterUtil;
import org.dromara.hutool.core.convert.ConvertUtil;
import org.dromara.hutool.core.util.ObjUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

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

        public <T> SqlCond(Class<T> entityType, FieldGetter<T, ?> field, @NonNull Op op, @NotNull Object expr) {
            super(SqlNodeType.COND);
            this.op = op;
            this.expr = expr;
            if (expr instanceof SqlOpExpr) {
                this.column = Tables.getColumnName(entityType, field);
            } else {
                var col = Tables.getColum(entityType, field);
                if (ObjUtil.isNotEmpty(expr)) {
                    var type1 = col.field().getType();
                    var type2 = expr.getClass();
                    if (expr instanceof Object[] arr) {
                        type2 = arr[0].getClass();
                    } else if (expr instanceof Collection<?> ls) {
                        type2 = IterUtil.getFirst(ls.iterator()).getClass();
                    }
                    if (type1.isPrimitive()) {
                        type1 = ConvertUtil.wrap(type1);
                    }
                    if (type1 != type2) {
                        throw new OrmException("「{}」字段类型「{}」和参数类型「{}」不匹配", entityType.getSimpleName(), type1.getSimpleName(), type2.getSimpleName());
                    }
                }
                this.column = col.name();
            }
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

        public SqlSet(TableColumn col, @Nullable Object value) {
            super(SqlNodeType.SET);
            check(col, value);
            this.expr = value;
            this.column = col.name();
        }

        public <T> SqlSet(Class<T> entityType, FieldGetter<T, ?> field, @Nullable Object value) {
            this(Tables.getColum(entityType, field), value);
        }

        protected void check(TableColumn col, @Nullable Object value) {
            if (ObjUtil.isNotEmpty(value)) {
                if (value instanceof Object[] || value instanceof Collection<?>) {
                    throw new OrmException("update 语句 set 不支持集合或数组");
                } else {
                    assert value != null;
                    var type1 = col.field().getType();
                    var type2 = value.getClass();
                    if (type1.isPrimitive()) {
                        type1 = ConvertUtil.wrap(type1);
                    }
                    if (type1 != type2) {
                        throw new OrmException("字段类型「{}」和参数类型「{}」不匹配", type1.getSimpleName(), type2.getSimpleName());
                    }
                }
            }
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
            var fn = expr.get();
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
