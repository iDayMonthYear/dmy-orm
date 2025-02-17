package cn.com.idmy.orm.core;

import cn.com.idmy.base.FieldGetter;
import cn.com.idmy.base.annotation.Column;
import cn.com.idmy.base.annotation.Id;
import cn.com.idmy.base.annotation.IdType;
import cn.com.idmy.base.annotation.Table;
import cn.com.idmy.base.util.LambdaUtil;
import cn.com.idmy.orm.OrmConfig;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.TableInfo.TableColumn;
import cn.com.idmy.orm.core.TableInfo.TableId;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.util.MapUtil;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.reflect.ClassUtil;
import org.dromara.hutool.core.reflect.FieldUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class Tables {
    private static final Map<Class<?>, TableInfo> mapperTables = new ConcurrentHashMap<>();
    private static final Map<Class<?>, TableInfo> entityTables = new ConcurrentHashMap<>();
    private static final Map<Field, TypeHandler<?>> typeHandlers = new ConcurrentHashMap<>();
    private static final OrmConfig config = OrmConfig.config();

    public static <T, R> void bindTypeHandler(@NotNull Class<T> entityType, @NotNull FieldGetter<T, R> getter, @NotNull TypeHandler<?> handler) {
        var fieldName = LambdaUtil.getFieldName(getter);
        var field = FieldUtil.getField(entityType, fieldName);
        field.setAccessible(true);
        typeHandlers.put(field, handler);
    }

    public static void clearTypeHandlers() {
        typeHandlers.clear();
    }

    @Nullable
    public static TypeHandler<?> getTypeHandler(@NotNull Field field) {
        return typeHandlers.get(field);
    }

    @NotNull
    public static TableInfo getTable(@NotNull Class<?> entityType) {
        return entityTables.computeIfAbsent(entityType, Tables::init);
    }

    @NotNull
    public static TableInfo getTable(@NotNull String className) {
        try {
            return getTableByMapperClass(Class.forName(className));
        } catch (ClassNotFoundException e) {
            throw new OrmException(e);
        }
    }

    @NotNull
    public static TableInfo getTableByMapperClass(@NotNull Class<?> mapperClass) {
        return MapUtil.computeIfAbsent(mapperTables, mapperClass, key -> getTable(ClassUtil.getTypeArgument(mapperClass)));
    }

    @NotNull
    private static TableInfo init(@NotNull Class<?> entityType) {
        final String tableName;
        final String tableTitle;
        Table table = null;
        if (entityType.isAnnotationPresent(Table.class)) {
            table = entityType.getAnnotation(Table.class);
            String name = table.name();
            tableName = StrUtil.isBlank(name) ? config.toTableName(entityType.getSimpleName()) : name;
            tableTitle = StrUtil.isBlank(table.value()) ? null : table.value();
        } else {
            tableName = config.toTableName(entityType.getSimpleName());
            tableTitle = null;
        }

        TableId tableId = null;
        var fields = FieldUtil.getFields(entityType);
        var columns = new ArrayList<TableColumn>();
        var columnMap = new HashMap<String, TableColumn>();
        for (var field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                if (tableId == null) {
                    var id = field.getAnnotation(Id.class);
                    var name = StrUtil.isBlank(id.name()) ? config.toColumnName(field.getName()) : id.name();
                    var idType = table == null || table.idType() == IdType.DEFAULT ? id.type() : table.idType();
                    if (idType == IdType.DEFAULT) {
                        idType = config.defaultIdType();
                    }
                    tableId = new TableId(field, name, idType, id.value(), id.before(), id.title());
                } else if (field.getClass() == entityType) {
                    throw new OrmException("实体类「{}」中存在多个主键", entityType.getName());
                }
            }

            if (!field.isAnnotationPresent(Column.class) || !field.getAnnotation(Column.class).ignore()) {
                Column column = field.getAnnotation(Column.class);
                String name = null;
                String title = null;
                if (column != null) {
                    name = column.name();
                    title = column.value();
                }
                if (StrUtil.isBlank(name)) {
                    name = config.toColumnName(field.getName());
                }
                if (StrUtil.isBlank(title)) {
                    title = null;
                }
                if (!columnMap.containsKey(field.getName())) {
                    var tableColumn = new TableColumn(field, name, title);
                    columns.add(tableColumn);
                    columnMap.put(field.getName(), tableColumn);
                }
            }
        }

        if (tableId == null) {
            throw new OrmException("实体类「{}」中不存在主键", entityType.getName());
        } else {
            return new TableInfo(entityType, tableName, tableId, tableTitle, columns.toArray(new TableColumn[0]), columnMap);
        }
    }

    @NotNull
    public static String getTableName(@NotNull Class<?> entityType) {
        return getTable(entityType).name();
    }

    @NotNull
    public static TableId getId(@NotNull Class<?> entityType) {
        return getTable(entityType).id();
    }

    @NotNull
    public static String getIdName(@NotNull Class<?> entityType) {
        return getId(entityType).name();
    }

    @NotNull
    public static String getIdName(@NotNull OrmDao<?, ?> dao) {
        return getId(dao.entityType()).name();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T getIdValue(@NotNull Object entity) {
        var table = getTable(entity.getClass());
        return (T) FieldUtil.getFieldValue(entity, table.id().field());
    }

    @NotNull
    public static Field getIdField(@NotNull Class<?> entityType) {
        return getTable(entityType).id().field();
    }

    @Nullable
    public static TableColumn getColum(@NotNull Class<?> entityType, @NotNull String fieldName) {
        var table = getTable(entityType);
        var columnMap = table.columnMap();
        if (CollUtil.isEmpty(columnMap)) {
            return null;
        } else {
            return columnMap.get(fieldName);
        }
    }

    @NotNull
    public static <T> TableColumn getColum(@NotNull Class<?> entityType, @NotNull FieldGetter<T, ?> field) {
        var table = getTable(entityType);
        var columnMap = table.columnMap();
        if (CollUtil.isEmpty(columnMap)) {
            throw new OrmException("实体类「{}」中不存在字段「{}」", entityType.getName(), field);
        } else {
            var fieldName = LambdaUtil.getFieldName(field);
            var tableColumn = columnMap.get(fieldName);
            if (tableColumn == null) {
                throw new OrmException("实体类「{}」中不存在字段「{}」", entityType.getName(), fieldName);
            } else {
                return tableColumn;
            }
        }
    }

    @Nullable
    public static String getColumnName(@NotNull Class<?> entityType, @NotNull String fieldName) {
        var colum = getColum(entityType, fieldName);
        if (colum == null) {
            return null;
        } else {
            return colum.name();
        }
    }

    @NotNull
    public static <T> String getColumnName(@NotNull Class<?> entityType, @NotNull FieldGetter<T, ?> field) {
        return getColum(entityType, field).name();
    }

    public static void clearTables() {
        entityTables.clear();
    }
}