package cn.com.idmy.orm.core.table;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author michael
 */
public class TableManager {
    private TableManager() {
    }

    @Getter
    @Setter
    private static DynamicTableProcessor dynamicTableProcessor;

    @Getter
    @Setter
    private static DynamicSchemaProcessor dynamicSchemaProcessor;

    private static final ThreadLocal<Map<String, String>> tableNameMapping = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, String>> schemaMapping = new ThreadLocal<>();

    public static void setHintTableMapping(String tableName, String mappingTable) {
        Map<String, String> hintTables = tableNameMapping.get();
        if (hintTables == null) {
            hintTables = new HashMap<>();
            tableNameMapping.set(hintTables);
        }
        hintTables.put(tableName, mappingTable);
    }

    public static String getHintTableMapping(String tableName) {
        return tableNameMapping.get().get(tableName);
    }

    public static void setHintSchemaMapping(String schema, String mappingSchema) {
        Map<String, String> hintTables = schemaMapping.get();
        if (hintTables == null) {
            hintTables = new HashMap<>();
            schemaMapping.set(hintTables);
        }
        hintTables.put(schema, mappingSchema);
    }

    public static String getHintSchemaMapping(String schema) {
        return schemaMapping.get().get(schema);
    }


    public static String getRealTable(String tableName) {
        Map<String, String> mapping = tableNameMapping.get();
        if (mapping != null) {
            String dynamicTableName = mapping.get(tableName);
            if (StrUtil.isNotBlank(dynamicTableName)) {
                return dynamicTableName;
            }
        }

        if (dynamicTableProcessor == null) {
            return tableName;
        }

        String dynamicTableName = dynamicTableProcessor.process(tableName);
        return StrUtil.isNotBlank(dynamicTableName) ? dynamicTableName : tableName;
    }


    public static String getRealSchema(String schema, String table) {
        Map<String, String> mapping = schemaMapping.get();
        if (mapping != null) {
            String dynamicSchema = mapping.get(schema);
            if (StrUtil.isNotBlank(dynamicSchema)) {
                return dynamicSchema;
            }
        }

        if (dynamicSchemaProcessor == null) {
            return schema;
        }

        String dynamicSchema = dynamicSchemaProcessor.process(schema, table);
        return StrUtil.isNotBlank(dynamicSchema) ? dynamicSchema : schema;
    }

    public static void clear() {
        tableNameMapping.remove();
        schemaMapping.remove();
    }
}
