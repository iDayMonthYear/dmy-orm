package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.TableInfo.TableColumn;
import cn.com.idmy.orm.mybatis.handler.TypeHandlerValue;
import org.dromara.hutool.core.reflect.FieldUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CreateSqlGenerator extends SqlGenerator {
    private final Object input;

    public CreateSqlGenerator(@NotNull Class<?> entityClass, @NotNull Object input) {
        super(entityClass, Collections.emptyList());
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

    @Nullable
    protected Object getTypeHandlerValue(@NotNull TableColumn column, @Nullable Object val) {
        var th = column.typeHandler();
        if (th == null || val == null) {
            return val;
        } else {
            return new TypeHandlerValue(th, val);
        }
    }

    private void genInsertHeader() {
        sql.append(SqlConsts.INSERT_INTO).append(SqlConsts.STRESS_MARK).append(tableName).append(SqlConsts.STRESS_MARK).append(SqlConsts.BLANK).append(SqlConsts.BRACKET_LEFT);
    }

    @NotNull
    private Pair<String, List<Object>> genInsert(@NotNull Object entity) {
        genInsertHeader();
        var table = Tables.getTable(entity.getClass());
        var columns = table.columns();

        // 收集非空字段
        var columnList = new ArrayList<String>();
        var valueList = new ArrayList<String>();
        params = new ArrayList<>();

        for (var col : columns) {
            var val = FieldUtil.getFieldValue(entity, col.field());
            if (val != null) {  // 只处理非空值
                columnList.add(SqlConsts.STRESS_MARK + col.name() + SqlConsts.STRESS_MARK);
                valueList.add(SqlConsts.PLACEHOLDER);
                params.add(getTypeHandlerValue(col, val));
            }
        }

        // 构建SQL
        sql.append(String.join(SqlConsts.DELIMITER, columnList))
                .append(SqlConsts.BRACKET_RIGHT)
                .append(SqlConsts.VALUES)
                .append(SqlConsts.BRACKET_LEFT)
                .append(String.join(SqlConsts.DELIMITER, valueList))
                .append(SqlConsts.BRACKET_RIGHT);

        return Pair.of(sql.toString(), params);
    }

    // 批量插入也需要修改
    @NotNull
    private Pair<String, List<Object>> genInsert(@NotNull Collection<?> entities) {
        if (entities.isEmpty()) {
            throw new IllegalArgumentException("实体集合不能为空");
        }

        genInsertHeader();
        var table = Tables.getTable(entityClass);
        var columns = table.columns();

        // 收集所有实体中出现的非空字段
        var columnIndices = new ArrayList<Integer>();

        for (int i = 0; i < columns.length; i++) {
            var col = columns[i];
            boolean hasNonNullValue = false;

            for (Object entity : entities) {
                if (FieldUtil.getFieldValue(entity, col.field()) != null) {
                    hasNonNullValue = true;
                    break;
                }
            }

            if (hasNonNullValue) {
                columnIndices.add(i);
                sql.append(SqlConsts.STRESS_MARK).append(col.name()).append(SqlConsts.STRESS_MARK).append(SqlConsts.DELIMITER);
            }
        }

        // 删除最后一个分隔符
        sql.setLength(sql.length() - SqlConsts.DELIMITER.length());
        sql.append(SqlConsts.BRACKET_RIGHT).append(SqlConsts.VALUES);

        // 构建值部分
        params = new ArrayList<>();

        for (Object entity : entities) {
            sql.append(SqlConsts.BRACKET_LEFT);
            for (int colIndex : columnIndices) {
                var col = columns[colIndex];
                var val = FieldUtil.getFieldValue(entity, col.field());
                params.add(getTypeHandlerValue(col, val));
                sql.append(SqlConsts.PLACEHOLDER).append(SqlConsts.DELIMITER);
            }
            // 删除最后一个分隔符
            sql.setLength(sql.length() - SqlConsts.DELIMITER.length());
            sql.append(SqlConsts.BRACKET_RIGHT).append(SqlConsts.DELIMITER);
        }

        // 删除最后一个分隔符
        sql.setLength(sql.length() - SqlConsts.DELIMITER.length());
        return Pair.of(sql.toString(), params);
    }
}