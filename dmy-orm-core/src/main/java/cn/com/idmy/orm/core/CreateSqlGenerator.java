package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.TableInfo.TableColumn;
import cn.com.idmy.orm.mybatis.handler.TypeHandlerValue;
import org.dromara.hutool.core.reflect.FieldUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

class CreateSqlGenerator extends SqlGenerator {
    protected final Object input;

    public CreateSqlGenerator(@NotNull Class<?> entityType, @NotNull Object input) {
        super(entityType, Collections.emptyList());
        this.input = input;
    }

    @Override
    protected @NotNull Pair<String, List<Object>> doGen() {
        if (input instanceof Collection<?> ls) {
            CrudInterceptors.interceptCreate(ls);
            return genInsert(ls);
        } else {
            CrudInterceptors.interceptCreate(input);
            return genInsert(input);
        }
    }

    @Nullable
    protected Object getTypeHandlerValue(@NotNull TableColumn col, @Nullable Object val) {
        var th = Tables.getTypeHandler(col.field());
        if (th == null || val == null) {
            return val;
        } else {
            return new TypeHandlerValue(th, val);
        }
    }

    private void genInsertHeader() {
        sql.append(INSERT_INTO).append(STRESS_MARK).append(tableName).append(STRESS_MARK).append(BLANK).append(BRACKET_LEFT);
    }

    @NotNull
    private Pair<String, List<Object>> genInsert(@NotNull Object entity) {
        genInsertHeader();
        var table = Tables.getTable(entity.getClass());
        var columns = table.columns();

        // 收集非空字段
        var cols = new ArrayList<String>();
        var vals = new ArrayList<String>();
        params = new ArrayList<>();

        for (int i = 0, size = columns.length; i < size; i++) {
            var col = columns[i];
            var val = FieldUtil.getFieldValue(entity, col.field());
            if (val != null) {
                cols.add(STRESS_MARK + col.name() + STRESS_MARK);
                vals.add(PLACEHOLDER);
                params.add(getTypeHandlerValue(col, val));
            }
        }
        sql.append(String.join(DELIMITER, cols))
                .append(BRACKET_RIGHT)
                .append(VALUES)
                .append(BRACKET_LEFT)
                .append(String.join(DELIMITER, vals))
                .append(BRACKET_RIGHT);
        if (params.isEmpty()) {
            throw new OrmException("插入数据不能为空");
        } else {
            return new Pair<>(sql.toString(), params);
        }
    }

    @NotNull
    private Pair<String, List<Object>> genInsert(@NotNull Collection<?> ls) {
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
            boolean hasNonNull = false;

            for (Object entity : ls) {
                if (FieldUtil.getFieldValue(entity, col.field()) != null) {
                    hasNonNull = true;
                    break;
                }
            }

            if (hasNonNull) {
                colIndices.add(i);
                sql.append(STRESS_MARK).append(col.name()).append(STRESS_MARK).append(DELIMITER);
            }
        }
        sql.setLength(sql.length() - DELIMITER.length());
        sql.append(BRACKET_RIGHT).append(VALUES);
        params = new ArrayList<>();
        for (Object entity : ls) {
            sql.append(BRACKET_LEFT);
            for (int colIdx : colIndices) {
                var col = cols[colIdx];
                var val = FieldUtil.getFieldValue(entity, col.field());
                params.add(getTypeHandlerValue(col, val));
                sql.append(PLACEHOLDER).append(DELIMITER);
            }
            sql.setLength(sql.length() - DELIMITER.length());
            sql.append(BRACKET_RIGHT).append(DELIMITER);
        }
        sql.setLength(sql.length() - DELIMITER.length());
        if (params.isEmpty()) {
            throw new OrmException("插入数据不能为空");
        } else {
            return new Pair<>(sql.toString(), params);
        }
    }
}