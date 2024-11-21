package cn.com.idmy.orm.core;

import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.annotation.Table;
import cn.com.idmy.orm.annotation.Table.Id;
import cn.com.idmy.orm.core.TableInfo.TableColumnInfo;
import cn.com.idmy.orm.core.TableInfo.TableIdInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.util.MapUtil;
import org.dromara.hutool.core.func.LambdaUtil;
import org.dromara.hutool.core.reflect.ClassUtil;
import org.dromara.hutool.core.reflect.FieldUtil;
import org.dromara.hutool.core.text.StrUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TableManager {
    private static final Map<Class<?>, TableInfo> mapperTableInfos = new ConcurrentHashMap<>();
    private static final Map<Class<?>, TableInfo> entityTableInfos = new ConcurrentHashMap<>();
    private static final Map<TypeHandlerKey, Class<? extends TypeHandler<?>>> typeHandlers = new ConcurrentHashMap<>();

    public static TableInfo getTableInfo(Class<?> entityClass) {
        return entityTableInfos.computeIfAbsent(entityClass, TableManager::init);
    }

    public static TableInfo getTableInfo(MappedStatement ms) {
        String mapperClassName = ms.getId().substring(0, ms.getId().lastIndexOf("."));
        try {
            Class<?> mapperClass = Class.forName(mapperClassName);
            return getTableInfoByMapperClass(mapperClass);
        } catch (ClassNotFoundException e) {
            throw new OrmException(e);
        }
    }

    public static TableInfo getTableInfoByMapperClass(Class<?> mapperClass) {
        return MapUtil.computeIfAbsent(mapperTableInfos, mapperClass, key -> {
            Class<?> entityClass = ClassUtil.getTypeArgument(mapperClass);
            return getTableInfo(entityClass);
        });
    }


    private static TableInfo init(Class<?> entityClass) {
        final String tableName;
        final String tableComment;
        if (entityClass.isAnnotationPresent(Table.class)) {
            var table = entityClass.getAnnotation(Table.class);
            var value = table.value();
            tableName = StrUtil.isBlank(value) ? entityClass.getSimpleName() : value;
            tableComment = table.comment();
        } else {
            tableName = entityClass.getSimpleName();
            tableComment = "";
        }

        var declaredFields = entityClass.getDeclaredFields();
        TableIdInfo idInfo = null;
        var columns = new ArrayList<TableColumnInfo>();
        var columnMap = new HashMap<String, TableColumnInfo>();
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(Table.Id.class)) {
                if (idInfo == null) {
                    var id = field.getAnnotation(Id.class);
                    var name = StrUtil.isBlank(id.name()) ? field.getName() : id.name();
                    idInfo = new TableIdInfo(field, name, id.value(), id.type(), id.before(), id.comment());
                } else {
                    throw new OrmException("实体类" + entityClass.getName() + "中存在多个主键");
                }
            } else if (!field.isAnnotationPresent(Table.Column.class) || !field.getAnnotation(Table.Column.class).ignore()) {
                var column = field.getAnnotation(Table.Column.class);
                String name;
                boolean large;
                boolean logicDelete;
                boolean tenant;
                boolean version;
                String comment;
                if (column == null) {
                    comment = null;
                    name = null;
                    large = false;
                    logicDelete = false;
                    tenant = false;
                    version = false;
                } else {
                    name = column.value();
                    large = column.large();
                    logicDelete = column.logicDelete();
                    tenant = column.tenant();
                    version = column.version();
                    comment = column.comment();
                }
                name = StrUtil.isBlank(name) ? field.getName() : name;
                var columnInfo = new TableColumnInfo(
                        field,
                        name,
                        large,
                        logicDelete,
                        version,
                        tenant,
                        comment
                );
                columns.add(columnInfo);
                columnMap.put(name, columnInfo);
            }
        }
        if (idInfo == null) {
            throw new OrmException("实体类" + entityClass.getName() + "中不存在主键");
        } else {
            return new TableInfo(entityClass, tableName, idInfo, tableComment, columns.toArray(new TableColumnInfo[0]), columnMap);
        }
    }

    public static String getTableName(Class<?> entityClass) {
        return getTableInfo(entityClass).name();
    }

    public static String getIdName(Class<?> entityClass) {
        return getTableInfo(entityClass).id().name();
    }

    public static <T> T getIdValue(Object entity) {
        TableInfo tableInfo = getTableInfo(entity.getClass());
        return (T) FieldUtil.getFieldValue(entity, tableInfo.id().name());
    }

    public static void clearTableInfo() {
        entityTableInfos.clear();
    }

    public static <T, R> void register(Class<T> entityClass, ColumnGetter<T, R> col, Class<? extends TypeHandler<?>> handlerClass) {
        var fieldName = LambdaUtil.getFieldName(col);
        typeHandlers.put(new TypeHandlerKey(entityClass, fieldName), handlerClass);
    }

    public static Class<? extends TypeHandler<?>> getHandler(Class<?> entityClass, String columnName) {
        return typeHandlers.get(new TypeHandlerKey(entityClass, columnName));
    }

    public static void clearTypeHandler() {
        typeHandlers.clear();
    }

    private record TypeHandlerKey(Class<?> entityClass, String columnName) {
    }
} 