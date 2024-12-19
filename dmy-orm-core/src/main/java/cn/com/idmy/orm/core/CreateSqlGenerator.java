package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.TableInfo.TableColumn;
import cn.com.idmy.orm.mybatis.handler.TypeHandlerValue;
import org.dromara.hutool.core.reflect.FieldUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CreateSqlGenerator extends SqlGenerator {
    private final Object input;

    public CreateSqlGenerator(Class<?> entityClass, Object input) {
        super(entityClass, null);
        this.input = input;
    }

    @Override
    protected Pair<String, List<Object>> doGenerate() {
        if (input instanceof Collection<?> ls) {
            // 调用批量插入拦截器
            CrudInterceptors.interceptCreate(ls);
            return genInsert(ls);
        } else {
            // 调用单个插入拦截器
            CrudInterceptors.interceptCreate(input);
            return genInsert(input);
        }
    }

    protected Object getTypeHandlerValue(TableColumn column, Object val) {
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

    private Pair<String, List<Object>> genInsert(Object entity) {
        genInsertHeader();
        var values = new StringBuilder(SqlConsts.VALUES).append(SqlConsts.BRACKET_LEFT);
        var table = Tables.getTable(entity.getClass());
        var columns = table.columns();
        params = new ArrayList<>();
        for (int i = 0, size = columns.length; i < size; i++) {
            var col = columns[i];
            var val = FieldUtil.getFieldValue(entity, col.field());
            sql.append(SqlConsts.STRESS_MARK).append(col.name()).append(SqlConsts.STRESS_MARK).append(SqlConsts.DELIMITER);
            values.append(SqlConsts.PLACEHOLDER).append(SqlConsts.DELIMITER);
            params.add(getTypeHandlerValue(col, val));
        }
        // 删除最后一个分隔符
        sql.setLength(sql.length() - SqlConsts.DELIMITER.length());
        values.setLength(values.length() - SqlConsts.DELIMITER.length());
        sql.append(SqlConsts.BRACKET_RIGHT).append(values).append(SqlConsts.BRACKET_RIGHT);
        return Pair.of(sql.toString(), params);
    }

    // 新增批量插入方法
    private Pair<String, List<Object>> genInsert(Collection<?> entities) {
        genInsertHeader();
        var cols = Tables.getTable(entityClass).columns();
        int colSize = cols.length;
        // 构建列名部分
        for (int i = 0; i < colSize; i++) {
            sql.append(SqlConsts.STRESS_MARK).append(cols[i].name()).append(SqlConsts.STRESS_MARK).append(SqlConsts.DELIMITER);
        }
        sql.setLength(sql.length() - SqlConsts.DELIMITER.length());
        sql.append(SqlConsts.BRACKET_RIGHT);

        // 构建values部分
        sql.append(SqlConsts.VALUES);
        params = new ArrayList<>(entities.size() * colSize); // 预分配空间
        for (Object entity : entities) { // 使用传统的 for 循环
            sql.append(SqlConsts.BRACKET_LEFT);
            for (int i = 0; i < colSize; i++) {
                var col = cols[i];
                var val = FieldUtil.getFieldValue(entity, col.field());
                params.add(getTypeHandlerValue(col, val));
                sql.append(SqlConsts.PLACEHOLDER).append(SqlConsts.DELIMITER);
            }
            sql.setLength(sql.length() - SqlConsts.DELIMITER.length()); // 删除最后一个分隔符
            sql.append(SqlConsts.BRACKET_RIGHT).append(SqlConsts.DELIMITER);
        }

        // 删除最后一个分隔符
        sql.setLength(sql.length() - SqlConsts.DELIMITER.length());
        return Pair.of(sql.toString(), params);
    }
}