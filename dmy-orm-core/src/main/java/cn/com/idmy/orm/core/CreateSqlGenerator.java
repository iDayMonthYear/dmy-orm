package cn.com.idmy.orm.core;

import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.TableInfo.TableColumn;
import cn.com.idmy.orm.mybatis.handler.TypeHandlerValue;
import org.dromara.hutool.core.lang.tuple.Pair;
import org.dromara.hutool.core.reflect.FieldUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

class CreateSqlGenerator extends SqlGenerator {
    protected final @NotNull Object input;

    public CreateSqlGenerator(@NotNull Class<?> entityType, @NotNull Object input) {
        super(entityType, Collections.emptyList());
        this.input = input;
    }

    @Override
    protected @NotNull Pair<String, List<Object>> doGenerate() {
        if (input instanceof Collection<?> ls) {
            CrudInterceptors.interceptCreate(ls);
            return genInsert(ls);
        } else {
            CrudInterceptors.interceptCreate(input);
            return genInsert(input);
        }
    }

    protected @Nullable Object getTypeHandlerValue(@NotNull TableColumn col, @Nullable Object val) {
        var th = Tables.getTypeHandler(col.field());
        if (th == null || val == null) {
            return val;
        } else {
            return new TypeHandlerValue(th, val);
        }
    }

    private void genInsertHeader() {
        sql.append(INSERT_INTO).append(tableInfo.schema()).append(STRESS_MARK).append(tableInfo.name()).append(STRESS_MARK).append(BLANK).append(BRACKET_LEFT);
    }

    private @NotNull Pair<String, List<Object>> genInsert(@NotNull Object entity) {
        genInsertHeader();
        var table = Tables.getTable(entity.getClass());
        var columns = table.columns();

        // 收集非空字段
        var cols = new ArrayList<String>();
        var vals = new ArrayList<String>();
        values = new ArrayList<>();

        for (int i = 0, size = columns.length; i < size; i++) {
            var col = columns[i];
            if (col.exist()) {
                var val = FieldUtil.getFieldValue(entity, col.field());
                if (val != null) {
                    cols.add(STRESS_MARK + col.name() + STRESS_MARK);
                    vals.add(PLACEHOLDER);
                    values.add(getTypeHandlerValue(col, val));
                }
            }
        }
        sql.append(String.join(DELIMITER, cols))
                .append(BRACKET_RIGHT)
                .append(VALUES)
                .append(BRACKET_LEFT)
                .append(String.join(DELIMITER, vals))
                .append(BRACKET_RIGHT);
        if (values.isEmpty()) {
            throw new OrmException("插入数据不能为空");
        } else {
            return Pair.of(sql.toString(), values);
        }
    }

    private @NotNull Pair<String, List<Object>> genInsert(@NotNull Collection<?> ls) {
        if (ls.isEmpty()) {
            throw new IllegalArgumentException("实体集合不能为空");
        }
        genInsertHeader();
        var table = Tables.getTable(entityType);
        var cols = table.columns();
        // 收集所有实体中出现的非空字段
        var colIndices = new ArrayList<Integer>();
        for (int i = 0; i < cols.length; i++) {
            var col = cols[i];
            if (col.exist()) {
                var hasNotNull = ls.stream().anyMatch(entity -> FieldUtil.getFieldValue(entity, col.field()) != null);
                if (hasNotNull) {
                    colIndices.add(i);
                    sql.append(STRESS_MARK).append(col.name()).append(STRESS_MARK).append(DELIMITER);
                }
            }
        }
        sql.setLength(sql.length() - DELIMITER.length());
        sql.append(BRACKET_RIGHT).append(VALUES);
        values = new ArrayList<>();
        for (var entity : ls) {
            sql.append(BRACKET_LEFT);
            for (int i = 0, size = colIndices.size(); i < size; i++) {
                var col = cols[colIndices.get(i)];
                var val = FieldUtil.getFieldValue(entity, col.field());
                values.add(getTypeHandlerValue(col, val));
                sql.append(PLACEHOLDER).append(DELIMITER);
            }
            sql.setLength(sql.length() - DELIMITER.length());
            sql.append(BRACKET_RIGHT).append(DELIMITER);
        }
        sql.setLength(sql.length() - DELIMITER.length());
        if (values.isEmpty()) {
            throw new OrmException("插入数据不能为空");
        } else {
            return Pair.of(sql.toString(), values);
        }
    }
}