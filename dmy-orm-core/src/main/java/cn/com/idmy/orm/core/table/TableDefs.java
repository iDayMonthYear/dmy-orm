package cn.com.idmy.orm.core.table;

import cn.com.idmy.orm.core.query.QueryColumn;
import cn.com.idmy.orm.core.util.ClassUtil;
import cn.com.idmy.orm.core.util.StringUtil;
import org.apache.ibatis.io.ResolverUtil;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author michael
 */
public class TableDefs implements Serializable {
    private static final Map<String, TableDef> TABLE_DEF_MAP = new HashMap<>();
    private static final Map<String, Map<String, QueryColumn>> QUERY_COLUMN_MAP = new HashMap<>();

    public static void init(String packageName) {
        ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
        resolverUtil.find(new ResolverUtil.IsA(TableDef.class), packageName);
        Set<Class<? extends Class<?>>> typeSet = resolverUtil.getClasses();
        for (Class<?> type : typeSet) {
            if (!type.isAnonymousClass() && !type.isInterface() && !type.isMemberClass()) {
                try {
                    registerTableDef(type);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static TableDef getTableDef(Class<?> entityClass, String tableNameWithSchema) {
        TableDef tableDef = TABLE_DEF_MAP.get(tableNameWithSchema);
        if (tableDef == null) {
            init(entityClass.getPackage().getName());
            tableDef = TABLE_DEF_MAP.get(tableNameWithSchema);
        }
        return tableDef;
    }


    public static QueryColumn getQueryColumn(Class<?> entityClass, String tableNameWithSchema, String column) {
        Map<String, QueryColumn> queryColumnMap = QUERY_COLUMN_MAP.get(tableNameWithSchema);
        if (queryColumnMap == null) {
            init(entityClass.getPackage().getName());
            queryColumnMap = QUERY_COLUMN_MAP.get(tableNameWithSchema);
        }
        return queryColumnMap != null ? queryColumnMap.get(column) : null;
    }


    public static void registerTableDef(Class<?> tableDefClass) throws IllegalAccessException {
        TableDef tableDef = (TableDef) ClassUtil.getFirstField(tableDefClass, field -> {
            int mod = Modifier.fieldModifiers();
            return Modifier.isPublic(mod) && Modifier.isStatic(mod);
        }).get(null);

        String key = StringUtil.buildSchemaWithTable(tableDef.getSchema(), tableDef.getTable());

        TABLE_DEF_MAP.put(key, tableDef);

        List<Field> allFields = ClassUtil.getAllFields(tableDef.getClass(), field -> field.getType() == QueryColumn.class);

        Map<String, QueryColumn> columnMap = new HashMap<>(allFields.size());
        for (Field field : allFields) {
            QueryColumn queryColumn = (QueryColumn) field.get(tableDef);
            columnMap.put(queryColumn.getName(), queryColumn);
        }

        QUERY_COLUMN_MAP.put(key, columnMap);
    }
}
