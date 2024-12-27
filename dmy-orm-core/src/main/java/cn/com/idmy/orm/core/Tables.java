package cn.com.idmy.orm.core;

import cn.com.idmy.base.annotation.Table;
import cn.com.idmy.base.annotation.Table.Column;
import cn.com.idmy.base.annotation.Table.Id;
import cn.com.idmy.base.annotation.Table.IdType;
import cn.com.idmy.base.util.LambdaUtil;
import cn.com.idmy.orm.OrmConfig;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.TableInfo.TableColumn;
import cn.com.idmy.orm.core.TableInfo.TableId;
import lombok.NoArgsConstructor;
import org.apache.ibatis.mapping.MappedStatement;
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
    private static final Map<Class<?>, TableInfo> mapperTableInfos = new ConcurrentHashMap<>();
    private static final Map<Class<?>, TableInfo> entityTableInfos = new ConcurrentHashMap<>();
    private static final Map<Field, TypeHandler<?>> typeHandlers = new ConcurrentHashMap<>();
    private static final OrmConfig config = OrmConfig.config();

    public static <T, R> void bindTypeHandler(@NotNull Class<T> entityClass, @NotNull FieldGetter<T, R> col, @NotNull TypeHandler<?> handler) {
        var fieldName = LambdaUtil.getFieldName(col);
        try {
            var field = entityClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            typeHandlers.put(field, handler);
        } catch (NoSuchFieldException e) {
            throw new OrmException("Field not found: " + fieldName, e);
        } catch (SecurityException e) {
            throw new OrmException("Access denied to field: " + fieldName, e);
        }
    }

    public static void clearTypeHandlers() {
        typeHandlers.clear();
    }

    @NotNull
    public static TableInfo getTable(@NotNull Class<?> entityClass) {
        return entityTableInfos.computeIfAbsent(entityClass, Tables::init);
    }

    @NotNull
    public static TableInfo getTable(@NotNull MappedStatement ms) {
        String className = ms.getId().substring(0, ms.getId().lastIndexOf("."));
        try {
            return getTableByMapperClass(Class.forName(className));
        } catch (ClassNotFoundException e) {
            throw new OrmException(e);
        }
    }

    @NotNull
    public static TableInfo getTableByMapperClass(@NotNull Class<?> mapperClass) {
        return MapUtil.computeIfAbsent(mapperTableInfos, mapperClass, key -> getTable(ClassUtil.getTypeArgument(mapperClass)));
    }

    @NotNull
    private static TableInfo init(@NotNull Class<?> entityClass) {
        final String tableName;
        final String tableComment;
        Table table = null;
        if (entityClass.isAnnotationPresent(Table.class)) {
            table = entityClass.getAnnotation(Table.class);
            String value = table.value();
            tableName = StrUtil.isBlank(value) ? config.toTableName(entityClass.getSimpleName()) : value;
            tableComment = table.comment();
        } else {
            tableName = config.toTableName(entityClass.getSimpleName());
            tableComment = "";
        }

        TableId tableId = null;
        var fields = entityClass.getDeclaredFields();
        var columns = new ArrayList<TableColumn>();
        var columnMap = new HashMap<String, TableColumn>();
        for (var field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                if (tableId == null) {
                    var id = field.getAnnotation(Id.class);
                    var name = StrUtil.isBlank(id.name()) ? config.toColumnName(field.getName()) : id.name();
                    var idType = table != null && table.idType() != IdType.DEFAULT ? table.idType() : id.type();
                    if (idType == IdType.DEFAULT) {
                        idType = config.defaultIdType();
                    }
                    tableId = new TableId(field, name, idType, id.value(), id.before(), id.comment());
                } else {
                    throw new OrmException("实体类" + entityClass.getName() + "中存在多个主键");
                }
            }

            if (!field.isAnnotationPresent(Column.class) || !field.getAnnotation(Column.class).ignore()) {
                Column column = field.getAnnotation(Column.class);
                String name = null;
                boolean large = false;
                String comment = "";
                if (column != null) {
                    name = column.value();
                    large = column.large();
                    comment = column.comment();
                }
                if (StrUtil.isBlank(name)) {
                    name = config.toColumnName(field.getName());
                }
                TypeHandler<?> handler = typeHandlers.get(field);
                TableColumn tableColumn = new TableColumn(field, name, large, comment, handler);
                columns.add(tableColumn);
                columnMap.put(name, tableColumn);
            }
        }

        if (tableId == null) {
            throw new OrmException("实体类" + entityClass.getName() + "中不存在主键");
        } else {
            return new TableInfo(entityClass, tableName, tableId, tableComment, columns.toArray(new TableColumn[0]), columnMap);
        }
    }

    @NotNull
    public static String getTableName(@NotNull Class<?> entityClass) {
        return getTable(entityClass).name();
    }

    @NotNull
    public static TableId getId(@NotNull Class<?> entityClass) {
        return getTable(entityClass).id();
    }

    @NotNull
    public static String getIdName(@NotNull Class<?> entityClass) {
        return getId(entityClass).name();
    }

    @NotNull
    public static String getIdName(@NotNull MybatisDao<?, ?> dao) {
        return getId(dao.entityClass()).name();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T getIdValue(@NotNull Object entity) {
        var table = getTable(entity.getClass());
        return (T) FieldUtil.getFieldValue(entity, table.id().field());
    }

    @NotNull
    public static Field getIdField(@NotNull Class<?> entityClass) {
        return getTable(entityClass).id().field();
    }

    @Nullable
    public static String getColumnName(@NotNull Class<?> entityClass, @NotNull String fieldName) {
        var table = getTable(entityClass);
        var columnMap = table.columnMap();
        if (CollUtil.isEmpty(columnMap)) {
            return null;
        } else {
            var column = columnMap.get(fieldName);
            return column == null ? null : column.name();
        }
    }

    @NotNull
    public static <T> String getColumnName(@NotNull Class<?> entityClass, @NotNull FieldGetter<T, ?> field) {
        var fieldName = LambdaUtil.getFieldName(field);
        var columnName = getColumnName(entityClass, fieldName);
        if (StrUtil.isBlank(columnName)) {
            throw new OrmException("实体类" + entityClass.getName() + "中不存在字段" + fieldName);
        } else {
            return columnName;
        }
    }

    public static void clearTables() {
        entityTableInfos.clear();
    }
}