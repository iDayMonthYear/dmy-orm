package cn.com.idmy.orm.core;

import cn.com.idmy.orm.annotation.Table;

import java.lang.reflect.Field;
import java.util.Map;

public record TableInfo(
        Class<?> entityClass,
        String tableName,
        String comment,
        Field idField,
        String idColumnName,
        Table.Id.Type idType,
        TableColumn[] columns,
        Map<String, TableColumn> columnMap) {
    public TableColumn getColumn(String columnName) {
        return columnMap.get(columnName);
    }

    public record TableColumn(
            Field field,
            String columnName,
            Class<?> columnType,
            boolean large,
            boolean logicDelete,
            boolean version,
            boolean tenant,
            String comment) {
    }
}