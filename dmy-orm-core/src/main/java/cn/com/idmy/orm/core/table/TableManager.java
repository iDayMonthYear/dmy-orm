package cn.com.idmy.orm.core.table;

import cn.com.idmy.orm.core.dialect.OperateType;
import cn.com.idmy.orm.core.util.StringUtil;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author michael
 */
public class TableManager {

    private TableManager() {
    }

    @Getter
    private static DynamicTableProcessor dynamicTableProcessor;
    @Getter
    private static DynamicSchemaProcessor dynamicSchemaProcessor;

    private static final ThreadLocal<Map<String, String>> tableNameMappingTL = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, String>> schemaMappingTL = new ThreadLocal<>();


    public static void setDynamicTableProcessor(DynamicTableProcessor dynamicTableProcessor) {
        TableManager.dynamicTableProcessor = dynamicTableProcessor;
    }

    public static void setDynamicSchemaProcessor(DynamicSchemaProcessor dynamicSchemaProcessor) {
        TableManager.dynamicSchemaProcessor = dynamicSchemaProcessor;
    }

    public static void setHintTableMapping(String tableName, String mappingTable) {
        Map<String, String> hintTables = tableNameMappingTL.get();
        if (hintTables == null) {
            hintTables = new HashMap<>();
            tableNameMappingTL.set(hintTables);
        }
        hintTables.put(tableName, mappingTable);
    }

    public static String getHintTableMapping(String tableName) {
        return tableNameMappingTL.get().get(tableName);
    }

    public static void setHintSchemaMapping(String schema, String mappingSchema) {
        Map<String, String> hintTables = schemaMappingTL.get();
        if (hintTables == null) {
            hintTables = new HashMap<>();
            schemaMappingTL.set(hintTables);
        }
        hintTables.put(schema, mappingSchema);
    }

    public static String getHintSchemaMapping(String schema) {
        return schemaMappingTL.get().get(schema);
    }


    public static String getRealTable(String tableName, OperateType operateType) {

        Map<String, String> mapping = tableNameMappingTL.get();
        if (mapping != null) {
            String dynamicTableName = mapping.get(tableName);
            if (StringUtil.isNotBlank(dynamicTableName)) {
                return dynamicTableName;
            }
        }

        if (dynamicTableProcessor == null) {
            return tableName;
        }

        String dynamicTableName = dynamicTableProcessor.process(tableName, operateType);
        return StringUtil.isNotBlank(dynamicTableName) ? dynamicTableName : tableName;
    }


    public static String getRealSchema(String schema, String table, OperateType operateType) {
        Map<String, String> mapping = schemaMappingTL.get();
        if (mapping != null) {
            String dynamicSchema = mapping.get(schema);
            if (StringUtil.isNotBlank(dynamicSchema)) {
                return dynamicSchema;
            }
        }

        if (dynamicSchemaProcessor == null) {
            return schema;
        }

        String dynamicSchema = dynamicSchemaProcessor.process(schema, table, operateType);
        return StringUtil.isNotBlank(dynamicSchema) ? dynamicSchema : schema;
    }

    public static void clear() {
        tableNameMappingTL.remove();
        schemaMappingTL.remove();
    }
}
