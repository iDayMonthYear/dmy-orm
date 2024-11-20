package cn.com.idmy.orm.util;

import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.annotation.Table;
import cn.com.idmy.orm.annotation.TableField;
import cn.com.idmy.orm.annotation.TableId;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.reflect.FieldUtil;
import org.dromara.hutool.core.reflect.TypeUtil;
import org.dromara.hutool.core.text.StrUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class OrmUtil {

    /**
     * 获取表名
     */
    public static String getTableName(Class<?> entityClass) {
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table table = entityClass.getAnnotation(Table.class);
            String value = table.value();
            return StrUtil.isBlank(value) ? entityClass.getSimpleName() : value;
        } else {
            return entityClass.getSimpleName();
        }
    }

    /**
     * 获取主键列名
     */
    public static String getId(Class<?> entityClass) {
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(TableId.class)) {
                return getFieldName(field);
            }
        }
        throw new OrmException("No primary key found in " + entityClass.getName());
    }

    /**
     * 获取主键值
     */
    public static Object getIdValue(Object entity) {
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(TableId.class)) {
                return FieldUtil.getFieldValue(entity, field);
            }
        }
        throw new OrmException("No primary key found in " + entity.getClass().getName());
    }

    /**
     * 获取所有列名(不包括主键)
     */
    public static List<String> findFields(Class<?> entityClass, Object entity) {
        List<String> columns = new ArrayList<>();
        Field[] fields = entityClass.getDeclaredFields();

        for (Field field : fields) {
            // 跳过主键字段
            if (field.isAnnotationPresent(TableId.class)) {
                continue;
            }

            // 跳过值为null的字段
            Object value = FieldUtil.getFieldValue(entity, field);
            if (value == null) {
                continue;
            }

            // 获取列名
            if (field.isAnnotationPresent(TableField.class)) {
                TableField tableField = field.getAnnotation(TableField.class);
                String column = tableField.value();
                columns.add(StrUtil.isBlank(column) ? field.getName() : column);
            } else {
                columns.add(field.getName());
            }
        }
        return columns;
    }

    /**
     * 获取所有字段值(不包括主键)
     */
    public static List<Object> findValues(Class<?> entityClass, Object entity) {
        List<Object> values = new ArrayList<>();
        Field[] fields = entityClass.getDeclaredFields();

        for (Field field : fields) {
            // 跳过主键字段
            if (field.isAnnotationPresent(TableId.class)) {
                continue;
            }

            // 跳过值为null的字段
            Object value = FieldUtil.getFieldValue(entity, field);
            if (value == null) {
                continue;
            }

            values.add(value);
        }
        return values;
    }

    /**
     * 获取字段名
     */
    public static String getFieldName(Field field) {
        if (field.isAnnotationPresent(TableField.class)) {
            TableField tableField = field.getAnnotation(TableField.class);
            String value = tableField.value();
            return StrUtil.isBlank(value) ? field.getName() : value;
        } else {
            return field.getName();
        }
    }

    public static TableId.Type getIdType(Class<?> entityClass) {
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(TableId.class)) {
                return field.getAnnotation(TableId.class).value();
            }
        }
        return TableId.Type.AUTO; // 默认为自增
    }

    public static Field getIdField(Class<?> entityClass) {
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(TableId.class)) {
                return field;
            }
        }
        throw new OrmException("No primary key found in " + entityClass.getName());
    }

    public static boolean shouldIncludeId(Class<?> entityClass) {
        TableId.Type type = getIdType(entityClass);
        return type != TableId.Type.AUTO; // 自增ID不需要包含在插入语句中
    }

    public static List<String> findInsertFields(Class<?> entityClass, Object entity) {
        List<String> fields = new ArrayList<>();
        Field[] declaredFields = entityClass.getDeclaredFields();

        for (Field field : declaredFields) {
            // 跳过自增主键
            if (field.isAnnotationPresent(TableId.class)) {
                if (!shouldIncludeId(entityClass)) {
                    continue;
                }
            }

            // 跳过值为null的字段
            Object value = FieldUtil.getFieldValue(entity, field);
            if (value == null) {
                continue;
            }

            // 获取列名
            if (field.isAnnotationPresent(TableField.class)) {
                TableField tableField = field.getAnnotation(TableField.class);
                String column = tableField.value();
                fields.add(StrUtil.isBlank(column) ? field.getName() : column);
            } else {
                fields.add(field.getName());
            }
        }
        return fields;
    }

    public static List<Object> findInsertValues(Class<?> entityClass, Object entity) {
        List<Object> values = new ArrayList<>();
        Field[] declaredFields = entityClass.getDeclaredFields();

        for (Field field : declaredFields) {
            // 跳过自增主键
            if (field.isAnnotationPresent(TableId.class)) {
                if (!shouldIncludeId(entityClass)) {
                    continue;
                }
            }

            // 跳过值为null的字段
            Object value = FieldUtil.getFieldValue(entity, field);
            if (value == null) {
                continue;
            }

            values.add(value);
        }
        return values;
    }

    public static String getIdFieldName(Class<?> entityClass) {
        Field idField = getIdField(entityClass);
        if (idField.isAnnotationPresent(TableField.class)) {
            TableField tableField = idField.getAnnotation(TableField.class);
            String value = tableField.value();
            return StrUtil.isBlank(value) ? idField.getName() : value;
        }
        return idField.getName();
    }

    public static Class<?> getEntityClass(Class<?> mapperClass) {
        return (Class<?>) TypeUtil.getTypeArgument(mapperClass);
    }
}
