package cn.com.idmy.orm.core;

import cn.com.idmy.base.annotation.Table;
import cn.com.idmy.orm.OrmConfig;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.TableInfo.TableColumnInfo;
import cn.com.idmy.orm.core.TableInfo.TableIdInfo;
import cn.com.idmy.orm.util.LambdaUtil;
import jakarta.annotation.Nullable;
import lombok.NoArgsConstructor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.util.MapUtil;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.reflect.ClassUtil;
import org.dromara.hutool.core.reflect.FieldUtil;
import org.dromara.hutool.core.text.StrUtil;

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

    public static <T, R> void bindTypeHandler(TypeHandler<?> handler, Class<T> entityClass, ColumnGetter<T, R> col) {
        String fieldName = LambdaUtil.getFieldName(col);
        try {
            Field field = entityClass.getDeclaredField(fieldName);
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

    public static TableInfo getTable(Class<?> entityClass) {
        return entityTableInfos.computeIfAbsent(entityClass, Tables::init);
    }

    public static TableInfo getTable(MappedStatement ms) {
        String className = ms.getId().substring(0, ms.getId().lastIndexOf("."));
        try {
            return getTableByMapperClass(Class.forName(className));
        } catch (ClassNotFoundException e) {
            throw new OrmException(e);
        }
    }

    public static TableInfo getTableByMapperClass(Class<?> mapperClass) {
        return MapUtil.computeIfAbsent(mapperTableInfos, mapperClass, key -> getTable(ClassUtil.getTypeArgument(mapperClass)));
    }

    private static TableInfo init(Class<?> entityClass) {
        final String tableName;
        final String tableComment;
        if (entityClass.isAnnotationPresent(Table.class)) {
            var table = entityClass.getAnnotation(Table.class);
            var value = table.value();
            if (StrUtil.isBlank(value)) {
                tableName = config.toTableName(entityClass.getSimpleName());
            } else {
                tableName = value;
            }
            tableComment = table.comment();
        } else {
            tableName = config.toTableName(entityClass.getSimpleName());
            tableComment = "";
        }

        TableIdInfo idInfo = null;
        var declaredFields = entityClass.getDeclaredFields();
        var columns = new ArrayList<TableColumnInfo>();
        var columnMap = new HashMap<String, TableColumnInfo>();
        for (var field : declaredFields) {
            if (field.isAnnotationPresent(Table.Id.class)) {
                if (idInfo == null) {
                    var id = field.getAnnotation(Table.Id.class);
                    String name;
                    if (StrUtil.isBlank(id.name())) {
                        name = config.toColumnName(field.getName());
                    } else {
                        name = id.name();
                    }
                    idInfo = new TableIdInfo(field, name, id.value(), id.type(), id.before(), id.comment());
                } else {
                    throw new OrmException("实体类" + entityClass.getName() + "中存在多个主键");
                }
            }

            if (!field.isAnnotationPresent(Table.Column.class) || !field.getAnnotation(Table.Column.class).ignore()) {
                var column = field.getAnnotation(Table.Column.class);
                String name;
                boolean large;
                String comment;
                if (column == null) {
                    comment = null;
                    name = null;
                    large = false;
                } else {
                    name = column.value();
                    large = column.large();
                    comment = column.comment();
                }
                if (StrUtil.isBlank(name)) {
                    name = config.toColumnName(field.getName());
                }
                var ci = new TableColumnInfo(
                        field,
                        name,
                        large,
                        comment,
                        typeHandlers.get(field)
                );
                columns.add(ci);
                columnMap.put(name, ci);
            }
        }
        if (idInfo == null) {
            throw new OrmException("实体类" + entityClass.getName() + "中不存在主键");
        } else {
            return new TableInfo(entityClass, tableName, idInfo, tableComment, columns.toArray(new TableColumnInfo[0]), columnMap);
        }
    }

    public static String getTableName(Class<?> entityClass) {
        return getTable(entityClass).name();
    }

    public static TableIdInfo getId(Class<?> entityClass) {
        TableIdInfo id = getTable(entityClass).id();
        if (id == null) {
            throw new OrmException(entityClass.getSimpleName() + "没有@Id注解");
        } else {
            return id;
        }
    }

    public static String getIdName(Class<?> entityClass) {
        return getId(entityClass).name();
    }

    public static String getIdName(MybatisDao<?, ?> dao) {
        return getId(dao.entityClass()).name();
    }

    @SuppressWarnings("unchecked")
    public static <T> T getIdValue(Object entity) {
        TableInfo tableInfo = getTable(entity.getClass());
        return (T) FieldUtil.getFieldValue(entity, tableInfo.id().name());
    }

    public static Field getIdField(Class<?> entityClass) {
        return getTable(entityClass).id().field();
    }

    @Nullable
    public static String getColumnName(Class<?> entityClass, String fieldName) {
        var ti = getTable(entityClass);
        var map = ti.columnMap();
        if (CollUtil.isEmpty(map)) {
            return null;
        }
        var ci = map.get(fieldName);
        if (ci == null) {
            return null;
        } else {
            return ci.name();
        }
    }

    public static void clearTables() {
        entityTableInfos.clear();
    }
}