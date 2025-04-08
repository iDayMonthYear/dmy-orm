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
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class Tables {
    private static final ConcurrentHashMap<Class<?>, TableInfo> mapperTables = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Class<?>, TableInfo> entityTables = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Field, TypeHandler<?>> typeHandlers = new ConcurrentHashMap<>();
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

    public static @Nullable TypeHandler<?> getTypeHandler(@NotNull Field field) {
        return typeHandlers.get(field);
    }

    public static @NotNull TableInfo getTable(@NotNull Class<?> entityType) {
        return entityTables.computeIfAbsent(entityType, Tables::init);
    }

    public static @Nullable TableInfo getTable(@NotNull String className) {
        try {
            return getTableByMapperClass(Class.forName(className));
        } catch (ClassNotFoundException e) {
            throw new OrmException(e);
        }
    }

    public static @Nullable TableInfo getTableByMapperClass(@NotNull Class<?> mapperClass) {
        return MapUtil.computeIfAbsent(mapperTables, mapperClass, key -> {
            var typeArgument = ClassUtil.getTypeArgument(mapperClass);
            if (typeArgument == null) {
                return null;
            } else {
                return getTable(typeArgument);
            }
        });
    }

    private static @NotNull TableInfo init(@NotNull Class<?> entityType) {
        final String tableName;
        final String tableTitle;
        Table table = null;
        if (entityType.isAnnotationPresent(Table.class)) {
            table = entityType.getAnnotation(Table.class);
            String name = table.name();
            tableName = StrUtil.isBlank(name) ? config.toTableName(entityType.getSimpleName()) : name;
            tableTitle = StrUtil.isBlank(table.title()) ? null : table.title();
        } else {
            tableName = config.toTableName(entityType.getSimpleName());
            tableTitle = null;
        }

        var ids = new ArrayList<TableId>();
        var fields = FieldUtil.getFields(entityType);
        var columns = new ArrayList<TableColumn>();
        var columnMap = new HashMap<String, TableColumn>();
        for (var field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                var id = field.getAnnotation(Id.class);
                var name = StrUtil.isBlank(id.name()) ? config.toColumnName(field.getName()) : id.name();
                var idType = table == null || table.idType() == IdType.DEFAULT ? id.type() : table.idType();
                if (idType == IdType.DEFAULT) {
                    idType = config.defaultIdType();
                }
                var key = id.key();
                if (StrUtil.isNotBlank(key)) {
                    idType = IdType.GENERATOR;
                }
                if (StrUtil.isBlank(key)) {
                    key = null;
                }
                var value = id.value();
                if (StrUtil.isBlank(value)) {
                    value = null;
                }
                var tableId = new TableId(field, name, idType, value, key, id.before(), id.title());
                ids.add(tableId);
            }

            if (!field.isAnnotationPresent(Column.class) || field.getAnnotation(Column.class) != null) {
                Column column = field.getAnnotation(Column.class);
                String name = null;
                String title = null;
                boolean exist = true;
                if (column != null) {
                    name = column.name();
                    title = column.value();
                    exist = column.exist();
                }
                if (StrUtil.isBlank(name)) {
                    name = config.toColumnName(field.getName());
                }
                if (StrUtil.isBlank(title)) {
                    title = null;
                }
                if (!columnMap.containsKey(field.getName())) {
                    var tableColumn = new TableColumn(field, name, title, exist);
                    columns.add(tableColumn);
                    columnMap.put(field.getName(), tableColumn);
                }
            }
        }

        if (ids.isEmpty()) {
            throw new OrmException("实体类「{}」中不存在主键", entityType.getName());
        } else {
            var schema = table == null ? "" : StrUtil.isBlank(table.schema()) ? "" : table.schema() + ".";
            var idArr = ids.toArray(new TableId[0]);
            var primaryId = idArr[0];
            return new TableInfo(entityType, schema, tableName, primaryId, idArr.length == 1 ? null : idArr, tableTitle, columns.toArray(new TableColumn[0]), columnMap);
        }
    }

    public static @NotNull String getTableName(@NotNull Class<?> entityType) {
        return getTable(entityType).name();
    }

    public static @NotNull TableId getId(@NotNull Class<?> entityType) {
        return getTable(entityType).id();
    }

    public static boolean isMultiIds(@NotNull Class<?> entityType) {
        return getTable(entityType).isMultiIds();
    }

    @NotNull
    public static String getIdColumnName(@NotNull Class<?> entityType) {
        return getId(entityType).name();
    }

    public static @NotNull String getIdColumnName(@NotNull OrmDao<?, ?> dao) {
        return getId(dao.entityType()).name();
    }

    @SuppressWarnings("unchecked")
    public static @Nullable <T> T getIdValue(@NotNull Object entity) {
        var table = getTable(entity.getClass());
        return (T) FieldUtil.getFieldValue(entity, table.id().field());
    }


    public static @NotNull Field getIdField(@NotNull Class<?> entityType) {
        return getTable(entityType).id().field();
    }

    public static @Nullable Field[] listIdFields(@NotNull Class<?> entityType) {
        var ids = getTable(entityType).ids();
        if (ids == null) {
            return null;
        } else {
            var fields = new Field[ids.length];
            for (int i = 0; i < ids.length; i++) {
                fields[i] = ids[i].field();
            }
            return fields;
        }
    }

    public static @Nullable Object[] listIdValues(@NotNull Object entity) {
        var table = getTable(entity.getClass());
        var ids = table.ids();
        if (ids == null) {
            return null;
        } else {
            var values = new Object[ids.length];
            for (int i = 0; i < ids.length; i++) {
                values[i] = FieldUtil.getFieldValue(entity, ids[i].field());
            }
            return values;
        }
    }

    public static @Nullable TableColumn getColum(@NotNull Class<?> entityType, @NotNull String fieldName) {
        var table = getTable(entityType);
        var columnMap = table.columnMap();
        if (CollUtil.isEmpty(columnMap)) {
            return null;
        } else {
            return columnMap.get(fieldName);
        }
    }

    public static @NotNull <T> TableColumn getColum(@NotNull Class<?> entityType, @NotNull FieldGetter<T, ?> field) {
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

    public static @Nullable String getColumnName(@NotNull Class<?> entityType, @NotNull String fieldName) {
        var colum = getColum(entityType, fieldName);
        if (colum == null) {
            return null;
        } else {
            return colum.name();
        }
    }

    public static @NotNull <T> String getColumnName(@NotNull Class<?> entityType, @NotNull FieldGetter<T, ?> field) {
        return getColum(entityType, field).name();
    }

    public static void clearTables() {
        entityTables.clear();
    }
}