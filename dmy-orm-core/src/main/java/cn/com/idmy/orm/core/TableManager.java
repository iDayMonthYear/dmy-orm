package cn.com.idmy.orm.core;

import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.annotation.Table;
import cn.com.idmy.orm.core.TableInfo.TableColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.type.TypeHandler;
import org.dromara.hutool.core.func.LambdaUtil;
import org.dromara.hutool.core.reflect.FieldUtil;
import org.dromara.hutool.core.text.StrUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TableManager {
    private static final Map<Class<?>, TableInfo> TABLE_INFO_CACHE = new ConcurrentHashMap<>();
    private static final Map<TypeHandlerKey, Class<? extends TypeHandler<?>>> TYPE_HANDLERS = new ConcurrentHashMap<>();

    public static TableInfo getTableInfo(Class<?> entityClass) {
        return TABLE_INFO_CACHE.computeIfAbsent(entityClass, TableManager::init);
    }

    private static TableInfo init(Class<?> entityClass) {
        final String tableName;
        final String comment;
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table table = entityClass.getAnnotation(Table.class);
            String value = table.value();
            tableName = StrUtil.isBlank(value) ? entityClass.getSimpleName() : value;
            comment = table.comment();
        } else {
            tableName = entityClass.getSimpleName();
            comment = "";
        }

        // 获取所有字段
        Field[] declaredFields = entityClass.getDeclaredFields();

        // 找出主键字段
        Field idField = null;
        String idColumnName = null;
        Table.Id.Type idType = null;
        var columns = new ArrayList<TableColumn>();
        var columnMap = new HashMap<String, TableColumn>();

        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(Table.Id.class)) {
                if (idField == null) {
                    idField = field;
                    Table.Id tableId = field.getAnnotation(Table.Id.class);
                    idType = tableId.value();
                    idColumnName = getColumnName(field);
                } else {
                    throw new OrmException("实体类" + entityClass.getName() + "中存在多个主键");
                }
            } else if (!field.isAnnotationPresent(Table.Column.class) || !field.getAnnotation(Table.Column.class).ignore()) {
                String columnName = getColumnName(field);
                Table.Column tableColumn = field.getAnnotation(Table.Column.class);
                TableColumn fieldInfo = new TableColumn(
                        field,
                        columnName,
                        field.getType(),
                        tableColumn.large(),
                        tableColumn.logicDelete(),
                        tableColumn.version(),
                        tableColumn.tenant(),
                        tableColumn.comment()
                );
                columns.add(fieldInfo);
                columnMap.put(columnName, fieldInfo);
            }
        }

        if (idField == null) {
            throw new OrmException("实体类" + entityClass.getName() + "未找到主键列");
        } else {
            return new TableInfo(entityClass, tableName, comment, idField, idColumnName, idType, columns.toArray(new TableColumn[0]), columnMap);
        }
    }

    public static String getTableName(Class<?> entityClass) {
        return getTableInfo(entityClass).tableName();
    }

    public static String getIdName(Class<?> entityClass) {
        return getTableInfo(entityClass).idColumnName();
    }

    public static <T> T getIdValue(Object entity) {
        TableInfo tableInfo = getTableInfo(entity.getClass());
        return (T) FieldUtil.getFieldValue(entity, tableInfo.idField());
    }

    public static String getColumnName(Field field) {
        if (field.isAnnotationPresent(Table.Column.class)) {
            Table.Column tableColumn = field.getAnnotation(Table.Column.class);
            String value = tableColumn.value();
            return StrUtil.isBlank(value) ? field.getName() : value;
        } else {
            return field.getName();
        }
    }

    public static void clearTableInfo() {
        TABLE_INFO_CACHE.clear();
    }


    public static <T, R> void register(Class<T> entityClass, ColumnGetter<T, R> col, Class<? extends TypeHandler<?>> handlerClass) {
        var fieldName = LambdaUtil.getFieldName(col);
        TYPE_HANDLERS.put(new TypeHandlerKey(entityClass, fieldName), handlerClass);
    }

    public static Class<? extends TypeHandler<?>> getHandler(Class<?> entityClass, String columnName) {
        return TYPE_HANDLERS.get(new TypeHandlerKey(entityClass, columnName));
    }

    public static void clearTypeHandler() {
        TYPE_HANDLERS.clear();
    }

    @Data
    @RequiredArgsConstructor
    @EqualsAndHashCode(of = {"entityClass", "columnName"})
    private static class TypeHandlerKey {
        private final Class<?> entityClass;
        private final String columnName;
    }
} 