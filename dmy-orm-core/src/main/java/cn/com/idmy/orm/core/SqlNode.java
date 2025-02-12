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

import static cn.com.idmy.orm.core.SqlFnName.COUNT;

@Data
@Accessors(fluent = true)
@RequiredArgsConstructor
public class SqlNode {
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

    @NotNull
    final SqlNode.Type type;

    public interface Column {
        @NotNull
        String column();
    }

    @Getter
    @Accessors(fluent = true)
    public static class Cond extends SqlNode implements Column {
        @NotNull
        final String column;
        @NotNull
        final Op op;
        @NotNull
        final Object expr;

        public Cond(@NotNull String col, @NonNull Op op, @NotNull Object expr) {
            super(Type.COND);
            this.column = SqlUtil.checkColumn(col);
            this.op = op;
            this.expr = expr;
        }

        public <T> Cond(Class<T> entityType, FieldGetter<T, ?> field, @NonNull Op op, @NotNull Object expr) {
            super(Type.COND);
            this.op = op;
            this.expr = expr;
            if (expr instanceof SqlOpExpr) {
                column = Tables.getColumnName(entityType, field);
            } else {
                var col = Tables.getColum(entityType, field);
                if (ObjUtil.isNotEmpty(expr) && op != Op.IS_NULL && op != Op.IS_NOT_NULL) {
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
                column = col.name();
            }
        }
    }

    public static class Or extends SqlNode {
        public Or() {
            super(Type.OR);
        }
    }

    @Getter
    @Accessors(fluent = true)
    public static class Set extends SqlNode implements Column {
        @NotNull
        final String column;
        @Nullable
        final Object expr;

        public Set(@NotNull String col, @Nullable Object expr) {
            super(Type.SET);
            this.column = SqlUtil.checkColumn(col);
            this.expr = expr;
        }

        public Set(@NotNull String col, @NonNull SqlOpExpr expr) {
            super(Type.SET);
            this.column = SqlUtil.checkColumn(col);
            this.expr = expr;
        }

        public Set(TableColumn col, @Nullable Object val) {
            super(Type.SET);
            check(col, val);
            this.expr = val;
            this.column = col.name();
        }

        public <T> Set(Class<T> entityType, FieldGetter<T, ?> field, @Nullable Object val) {
            this(Tables.getColum(entityType, field), val);
        }

        protected void check(TableColumn col, @Nullable Object val) {
            if (ObjUtil.isNotEmpty(val)) {
                if (val instanceof Object[] || val instanceof Collection<?>) {
                    throw new OrmException("update 语句 set 不支持集合或数组");
                } else {
                    assert val != null;
                    var type1 = col.field().getType();
                    var type2 = val.getClass();
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
    public static class GroupBy extends SqlNode implements Column {
        @NotNull
        final String column;

        public GroupBy(@NotNull String col) {
            super(Type.GROUP_BY);
            this.column = SqlUtil.checkColumn(col);
        }
    }

    @Getter
    @Accessors(fluent = true)
    public static class OrderBy extends SqlNode implements Column {
        @NotNull
        final String column;
        final boolean desc;

        public OrderBy(@NotNull String col, boolean desc) {
            super(Type.ORDER_BY);
            this.column = SqlUtil.checkColumn(col);
            this.desc = desc;
        }
    }

    @Getter
    @Accessors(fluent = true)
    public static class SelectColumn extends SqlNode implements Column {
        @NotNull
        String column;
        @Nullable
        SqlFnExpr<?> expr;

        public SelectColumn(@NotNull String col) {
            super(Type.SELECT_COLUMN);
            this.column = SqlUtil.checkColumn(col);
        }

        public SelectColumn(@NonNull SqlFnExpr<?> expr) {
            super(Type.SELECT_COLUMN);
            this.expr = expr;
            var fn = expr.get();
            var name = fn.name();
            column = name == COUNT ? "*" : fn.column();
        }

        public SelectColumn(@NonNull SqlFnExpr<?> expr, @NotNull String alias) {
            this(expr);
            column = SqlUtil.checkColumn(alias);
        }
    }

    @Getter
    @Accessors(fluent = true)
    public static class Distinct extends SqlNode implements Column {
        @NotNull
        String column;

        public Distinct() {
            super(Type.DISTINCT);
            this.column = "";
        }

        public Distinct(@NotNull String col) {
            this();
            this.column = SqlUtil.checkColumn(col);
        }
    }
}
