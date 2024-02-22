package cn.com.idmy.orm.core.table;

import cn.com.idmy.orm.annotation.Column;
import cn.com.idmy.orm.annotation.Id;
import cn.com.idmy.orm.annotation.Table;
import cn.com.idmy.orm.core.BaseMapper;
import cn.com.idmy.orm.core.OrmConfig;
import cn.com.idmy.orm.core.exception.OrmExceptions;
import cn.com.idmy.orm.core.query.QueryChain;
import cn.com.idmy.orm.core.query.QueryColumn;
import cn.com.idmy.orm.core.query.QueryCondition;
import cn.com.idmy.orm.core.query.QueryWrapper;
import cn.com.idmy.orm.core.util.CollectionUtil;
import cn.com.idmy.orm.core.util.Reflectors;
import cn.com.idmy.orm.core.util.StringUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.reflection.TypeParameterResolver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.*;
import org.apache.ibatis.util.MapUtil;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.time.chrono.JapaneseDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TableInfoFactory {
    public static final Set<Class<?>> defaultSupportColumnTypes = CollectionUtil.newHashSet(int.class, Integer.class, short.class, Short.class, long.class, Long.class, float.class, Float.class, double.class, Double.class, boolean.class, Boolean.class, Date.class, java.sql.Date.class, Time.class, Timestamp.class, Instant.class, LocalDate.class, LocalDateTime.class, LocalTime.class, OffsetDateTime.class, OffsetTime.class, ZonedDateTime.class, Year.class, Month.class, YearMonth.class, JapaneseDate.class, byte[].class, Byte[].class, Byte.class, BigInteger.class, BigDecimal.class, char.class, String.class, Character.class);
    private static final Set<Class<?>> ignoreColumnTypes = CollectionUtil.newHashSet(QueryWrapper.class, QueryColumn.class, QueryCondition.class, QueryChain.class);
    private static final Map<Class<?>, TableInfo> mapperTableInfoMap = new ConcurrentHashMap<>();
    private static final Map<Class<?>, TableInfo> entityTableMap = new ConcurrentHashMap<>();
    private static final Map<String, TableInfo> tableInfoMap = new ConcurrentHashMap<>();
    private static final Set<String> initialledPackageNames = new HashSet<>();

    public synchronized static void init(String mapperPackageName) {
        if (!initialledPackageNames.contains(mapperPackageName)) {
            ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
            resolverUtil.find(new ResolverUtil.IsA(BaseMapper.class), mapperPackageName);
            Set<Class<? extends Class<?>>> mapperSet = resolverUtil.getClasses();
            for (Class<? extends Class<?>> mapperClass : mapperSet) {
                ofMapperClass(mapperClass);
            }
            initialledPackageNames.add(mapperPackageName);
        }
    }

    public static TableInfo ofMapperClass(Class<?> mapperClass) {
        return MapUtil.computeIfAbsent(mapperTableInfoMap, mapperClass, key -> {
            Class<?> entityClass = getEntityClass(mapperClass);
            return entityClass == null ? null : ofEntityClass(entityClass);
        });
    }

    public static TableInfo ofEntityClass(Class<?> entityClass) {
        return MapUtil.computeIfAbsent(entityTableMap, entityClass, aClass -> {
            TableInfo tableInfo = createTableInfo(entityClass);
            tableInfoMap.put(tableInfo.getTableNameWithSchema(), tableInfo);
            return tableInfo;
        });
    }


    public static TableInfo ofTableName(String tableName) {
        return StrUtil.isNotBlank(tableName) ? tableInfoMap.get(tableName) : null;
    }

    @Nullable
    private static Class<?> getEntityClass(Class<?> mapperClass) {
        if (mapperClass == null || mapperClass == Object.class) {
            return null;
        } else {
            return getEntityClass(mapperClass, null);
        }
    }

    @Nullable
    private static Class<?> getEntityClass(Class<?> mapperClass, Type[] actualTypeArguments) {
        // 检查基接口
        Type[] genericInterfaces = mapperClass.getGenericInterfaces();
        for (Type type : genericInterfaces) {
            if (type instanceof ParameterizedType parameterizedType) {
                // 泛型基接口
                Type rawType = parameterizedType.getRawType();
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                adjustTypeArguments(mapperClass, actualTypeArguments, typeArguments);
                if (rawType == BaseMapper.class) {
                    // 找到了
                    if (typeArguments[0] instanceof Class) {
                        return (Class<?>) typeArguments[0];
                    }
                } else if (rawType instanceof Class) {
                    // 其他泛型基接口
                    Class<?> entityClass = getEntityClass((Class<?>) rawType, typeArguments);
                    if (entityClass != null) {
                        return entityClass;
                    }
                }
            } else if (type instanceof Class) {
                // 其他基接口
                Class<?> entityClass = getEntityClass((Class<?>) type);
                if (entityClass != null) {
                    return entityClass;
                }
            }
        }
        // 检查基类
        Class<?> superclass = mapperClass.getSuperclass();
        if (superclass == null || superclass == Object.class) {
            return null;
        } else {
            Type[] typeArguments = superclass.getTypeParameters();
            adjustTypeArguments(mapperClass, actualTypeArguments, typeArguments);
            return getEntityClass(superclass, typeArguments);
        }
    }

    private static void adjustTypeArguments(Class<?> subclass, Type[] subclassTypeArguments, Type[] typeArguments) {
        for (int i = 0; i < typeArguments.length; i++) {
            if (typeArguments[i] instanceof TypeVariable<?> typeVariable) {
                TypeVariable<?>[] typeParameters = subclass.getTypeParameters();
                for (int j = 0; j < typeParameters.length; j++) {
                    if (Objects.equals(typeVariable.getName(), typeParameters[j].getName())) {
                        typeArguments[i] = subclassTypeArguments[j];
                        break;
                    }
                }
            }
        }
    }

    private static TableInfo createTableInfo(Class<?> entityClass) {
        TableInfo tableInfo = new TableInfo();
        tableInfo.setEntityClass(entityClass);
        Reflector reflector = Reflectors.of(entityClass);
        tableInfo.setReflector(reflector);

        //初始化表名
        Table table = entityClass.getAnnotation(Table.class);
        if (table == null) {
            //默认为类名转驼峰下划线
            tableInfo.setTableName(StringUtil.camelToUnderline(entityClass.getSimpleName()));
        } else {
            tableInfo.setSchema(table.schema());
            tableInfo.setTableName(table.value());
            tableInfo.setCamelToUnderline(table.camelToUnderline());
            if (StrUtil.isNotBlank(table.dataSource())) {
                tableInfo.setDataSource(table.dataSource());
            }
        }

        //初始化字段相关
        List<ColumnInfo> columnInfoList = new ArrayList<>();
        List<IdInfo> idInfos = new ArrayList<>();

        String logicDeleteColumn = null;
        String versionColumn = null;
        String tenantIdColumn = null;

        //数据插入时，默认插入数据字段
        Map<String, String> onInsertColumns = new HashMap<>();

        //数据更新时，默认更新内容的字段
        Map<String, String> onUpdateColumns = new HashMap<>();

        //大字段列
        Set<String> largeColumns = new LinkedHashSet<>();

        // 默认查询列
        Set<String> defaultQueryColumns = new LinkedHashSet<>();

        List<Field> entityFields = getColumnFields(entityClass);

        OrmConfig config = OrmConfig.getDefaultConfig();

        for (Field field : entityFields) {
            Class<?> fieldType = reflector.getGetterType(field.getName());

            //移除默认的忽略字段
            boolean isIgnoreField = false;
            for (Class<?> ignoreColumnType : ignoreColumnTypes) {
                if (ignoreColumnType.isAssignableFrom(fieldType)) {
                    isIgnoreField = true;
                    break;
                }
            }

            if (isIgnoreField) {
                continue;
            }

            Column columnAnnotation = field.getAnnotation(Column.class);


            //满足以下 3 种情况，不支持该类型的属性自动映射为字段
            if ((columnAnnotation == null || columnAnnotation.typeHandler() == UnknownTypeHandler.class) // 未配置 typeHandler
                    && !fieldType.isEnum()   // 类型不是枚举
                    && !defaultSupportColumnTypes.contains(fieldType) //默认的自动类型不包含该类型
            ) {
                // 忽略 集合 实体类 解析
                if (columnAnnotation != null && columnAnnotation.ignore()) {
                    continue;
                }
                // 集合嵌套
                if (Collection.class.isAssignableFrom(fieldType)) {
                    Type genericType = TypeParameterResolver.resolveFieldType(field, entityClass);
                    if (genericType instanceof ParameterizedType) {
                        Type actualTypeArgument = ((ParameterizedType) genericType).getActualTypeArguments()[0];
                        if (actualTypeArgument instanceof Class) {
                            tableInfo.addCollectionType(field, (Class<?>) actualTypeArgument);
                        }
                    }
                }
                // 实体类嵌套
                else if (!Map.class.isAssignableFrom(fieldType) && !fieldType.isArray()) {
                    tableInfo.addAssociationType(field.getName(), fieldType);
                }
                // 不支持的类型直接跳过
                continue;
            }

            //列名
            String columnName = getColumnName(tableInfo.isCamelToUnderline(), field, columnAnnotation);

            //逻辑删除字段
            if ((columnAnnotation != null && columnAnnotation.isLogicDelete()) || columnName.equals(config.getLogicDeleteColumn())) {
                if (logicDeleteColumn == null) {
                    logicDeleteColumn = columnName;
                } else {
                    throw OrmExceptions.wrap("The logic delete column of entity[%s] must be less then 2.", entityClass.getName());
                }
            }

            //乐观锁版本字段
            if ((columnAnnotation != null && columnAnnotation.version()) || columnName.equals(config.getVersionColumn())) {
                if (versionColumn == null) {
                    versionColumn = columnName;
                } else {
                    throw OrmExceptions.wrap("The version column of entity[%s] must be less then 2.", entityClass.getName());
                }
            }

            //租户ID 字段
            if ((columnAnnotation != null && columnAnnotation.tenantId()) || columnName.equals(config.getTenantColumn())) {
                if (tenantIdColumn == null) {
                    tenantIdColumn = columnName;
                } else {
                    throw OrmExceptions.wrap("The tenantId column of entity[%s] must be less then 2.", entityClass.getName());
                }
            }


            if (columnAnnotation != null && StrUtil.isNotBlank(columnAnnotation.onInsertValue())) {
                onInsertColumns.put(columnName, columnAnnotation.onInsertValue().trim());
            }


            if (columnAnnotation != null && StrUtil.isNotBlank(columnAnnotation.onUpdateValue())) {
                onUpdateColumns.put(columnName, columnAnnotation.onUpdateValue().trim());
            }


            if (columnAnnotation != null && columnAnnotation.isLarge()) {
                largeColumns.add(columnName);
            }

            //主键配置
            Id id = field.getAnnotation(Id.class);
            ColumnInfo columnInfo;
            if (id == null) {
                columnInfo = new ColumnInfo();
                columnInfoList.add(columnInfo);
            } else {
                columnInfo = new IdInfo(id);
                idInfos.add((IdInfo) columnInfo);
            }


            if (columnAnnotation != null && ArrayUtil.isNotEmpty(columnAnnotation.alias())) {
                columnInfo.setAlias(columnAnnotation.alias());
            }

            columnInfo.setColumn(columnName);
            columnInfo.setProperty(field.getName());
            columnInfo.setPropertyType(fieldType);
            columnInfo.setIgnore(columnAnnotation != null && columnAnnotation.ignore());

            // 默认查询列 没有忽略且不是大字段
            if (columnAnnotation == null || (!columnAnnotation.isLarge() && !columnAnnotation.ignore())) {
                defaultQueryColumns.add(columnName);
            }

            //typeHandler 配置
            if (columnAnnotation != null && columnAnnotation.typeHandler() != UnknownTypeHandler.class) {
                TypeHandler<?> typeHandler = null;

                //集合类型，支持泛型
                //fixed https://gitee.com/mybatis-flex/mybatis-flex/issues/I7S2YE
                if (Collection.class.isAssignableFrom(fieldType)) {
                    typeHandler = createCollectionTypeHandler(entityClass, field, columnAnnotation.typeHandler(), fieldType);
                }

                //非集合类型
                else {
                    Class<?> typeHandlerClass = columnAnnotation.typeHandler();
                    Configuration configuration = config.getConfiguration();
                    if (configuration != null) {
                        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
                        Class<?> propertyType = columnInfo.getPropertyType();
                        JdbcType jdbcType = columnAnnotation.jdbcType();
                        if (jdbcType != JdbcType.UNDEFINED) {
                            typeHandler = typeHandlerRegistry.getTypeHandler(propertyType, jdbcType);
                        }
                        if (typeHandler == null || !typeHandlerClass.isAssignableFrom(typeHandler.getClass())) {
                            typeHandler = typeHandlerRegistry.getInstance(propertyType, typeHandlerClass);
                        }
                    }
                }

                columnInfo.setTypeHandler(typeHandler);
            }

            // 数据脱敏配置
            if (columnAnnotation != null && StrUtil.isNotBlank(columnAnnotation.mask())) {
                if (String.class != fieldType) {
                    throw new IllegalStateException("@mask only support for string type field. error: " + entityClass.getName() + "." + field.getName());
                } else {
                    columnInfo.setMaskType(columnAnnotation.mask().trim());
                }
            }

            // jdbcType 配置
            if (columnAnnotation != null && columnAnnotation.jdbcType() != JdbcType.UNDEFINED) {
                columnInfo.setJdbcType(columnAnnotation.jdbcType());
            }
        }

        tableInfo.setLogicDeleteColumn(logicDeleteColumn);
        tableInfo.setVersionColumn(versionColumn);
        tableInfo.setTenantIdColumn(tenantIdColumn);

        if (!onInsertColumns.isEmpty()) {
            tableInfo.setOnInsertColumns(onInsertColumns);
        }

        if (!onUpdateColumns.isEmpty()) {
            tableInfo.setOnUpdateColumns(onUpdateColumns);
        }

        if (!largeColumns.isEmpty()) {
            tableInfo.setLargeColumns(largeColumns.toArray(new String[0]));
        }

        if (!defaultQueryColumns.isEmpty()) {
            tableInfo.setDefaultQueryColumns(defaultQueryColumns.toArray(new String[0]));
        }

        // 此处需要保证顺序先设置 PrimaryKey，在设置其他 Column，
        // 否则会影响 SQL 的字段构建顺序
        tableInfo.setPrimaryKeyList(idInfos);
        tableInfo.setColumnInfoList(columnInfoList);
        return tableInfo;
    }

    /**
     * 创建 typeHandler
     * 参考 {@link TypeHandlerRegistry#getInstance(Class, Class)}
     *
     * @param entityClass
     * @param field
     * @param typeHandlerClass
     * @param fieldType
     */
    private static TypeHandler<?> createCollectionTypeHandler(Class<?> entityClass, Field field, Class<?> typeHandlerClass, Class<?> fieldType) {
        Class<?> genericClass = null;
        Type genericType = TypeParameterResolver.resolveFieldType(field, entityClass);
        if (genericType instanceof ParameterizedType) {
            Type actualTypeArgument = ((ParameterizedType) genericType).getActualTypeArguments()[0];
            if (actualTypeArgument instanceof Class) {
                genericClass = (Class<?>) actualTypeArgument;
            }
        }

        try {
            Constructor<?> constructor = typeHandlerClass.getConstructor(Class.class, Class.class);
            return (TypeHandler<?>) constructor.newInstance(fieldType, genericClass);
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            throw new TypeException("Failed invoking constructor for handler " + typeHandlerClass, e);
        }
        try {
            Constructor<?> constructor = typeHandlerClass.getConstructor(Class.class);
            return (TypeHandler<?>) constructor.newInstance(fieldType);
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            throw new TypeException("Failed invoking constructor for handler " + typeHandlerClass, e);
        }
        try {
            Constructor<?> c = typeHandlerClass.getConstructor();
            return (TypeHandler<?>) c.newInstance();
        } catch (Exception e) {
            throw new TypeException("Unable to find a usable constructor for " + typeHandlerClass, e);
        }
    }


    static String getColumnName(boolean isCamelToUnderline, Field field, Column column) {
        if (column != null && StrUtil.isNotBlank(column.value())) {
            return column.value();
        } else if (isCamelToUnderline) {
            return StringUtil.camelToUnderline(field.getName());
        } else {
            return field.getName();
        }
    }

    public static List<Field> getColumnFields(Class<?> entityClass) {
        List<Field> fields = new ArrayList<>();
        doGetFields(entityClass, fields);
        return fields;
    }


    private static void doGetFields(Class<?> entityClass, List<Field> fields) {
        if (entityClass == null || entityClass == Object.class) {
            return;
        }

        Field[] declaredFields = entityClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (!Modifier.isStatic(declaredField.getModifiers()) && !existName(fields, declaredField)) {
                fields.add(declaredField);
            }
        }

        doGetFields(entityClass.getSuperclass(), fields);
    }

    private static boolean existName(List<Field> fields, Field field) {
        return fields.stream().anyMatch(f -> f.getName().equalsIgnoreCase(field.getName()));
    }
}
