package cn.com.idmy.orm.core.provider;

import cn.com.idmy.orm.core.OrmConsts;
import cn.com.idmy.orm.core.exception.OrmAssert;
import cn.com.idmy.orm.core.exception.OrmExceptions;
import cn.com.idmy.orm.core.exception.locale.LocalizedFormats;
import cn.com.idmy.orm.core.query.QueryWrapper;
import cn.com.idmy.orm.core.row.Row;
import cn.com.idmy.orm.core.table.TableInfo;
import cn.com.idmy.orm.core.table.TableInfoFactory;
import cn.com.idmy.orm.core.util.StringUtil;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
class ProviderUtil {

    private ProviderUtil() {
    }

    public static String getSqlString(Map params) {
        return (String) params.get(OrmConsts.SQL);
    }

    public static void flatten(Map params) {
        Object[] o = (Object[]) params.get(OrmConsts.SQL_ARGS);
        Object map;
        if (o != null && o.length == 1 && (map = o[0]) instanceof Map) {
            params.putAll((Map) map);
            params.put(OrmConsts.RAW_ARGS, Boolean.TRUE);
        }
    }

    public static void setSqlArgs(Map params, Object[] args) {
        params.put(OrmConsts.SQL_ARGS, args);
    }

    public static String getSchemaName(Map params) {
        Object schemaNameObj = params.get(OrmConsts.SCHEMA_NAME);
        return schemaNameObj != null ? schemaNameObj.toString().trim() : null;
    }

    public static String getTableName(Map params) {
        Object tableNameObj = params.get(OrmConsts.TABLE_NAME);
        return tableNameObj != null ? tableNameObj.toString().trim() : null;
    }

    public static String[] getPrimaryKeys(Map params) {
        String primaryKey = (String) params.get(OrmConsts.PRIMARY_KEY);
        if (StringUtil.isBlank(primaryKey)) {
            throw OrmExceptions.wrap(LocalizedFormats.OBJECT_NULL_OR_BLANK, "primaryKey");
        }
        String[] primaryKeys = primaryKey.split(",");
        for (int i = 0; i < primaryKeys.length; i++) {
            primaryKeys[i] = primaryKeys[i].trim();
        }
        return primaryKeys;
    }

    public static Object[] getPrimaryValues(Map params) {
        Object primaryValue = params.get(OrmConsts.PRIMARY_VALUE);
        if (primaryValue == null) {
            return OrmConsts.EMPTY_ARRAY;
        }
        if (primaryValue.getClass().isArray()) {
            return (Object[]) primaryValue;
        } else if (primaryValue instanceof Collection) {
            return ((Collection<?>) primaryValue).toArray();
        } else {
            return new Object[]{primaryValue};
        }
    }

    public static QueryWrapper getQueryWrapper(Map params) {
        Object queryWrapper = params.get(OrmConsts.QUERY);
        OrmAssert.notNull(queryWrapper, "queryWrapper");
        return (QueryWrapper) queryWrapper;
    }

    public static Row getRow(Map params) {
        return (Row) params.get(OrmConsts.ROW);
    }

    public static List<Row> getRows(Map params) {
        return (List<Row>) params.get(OrmConsts.ROWS);
    }

    public static TableInfo getTableInfo(ProviderContext context) {
        return TableInfoFactory.ofMapperClass(context.getMapperType());
    }

    public static Object getEntity(Map params) {
        return params.get(OrmConsts.ENTITY);
    }

    public static String getFieldName(Map params) {
        return (String) params.get(OrmConsts.FIELD_NAME);
    }

    public static Object getValue(Map params) {
        return params.get(OrmConsts.VALUE);
    }

    public static List<Object> getEntities(Map params) {
        return (List<Object>) params.get(OrmConsts.ENTITIES);
    }

    public static boolean isIgnoreNulls(Map params) {
        return params.containsKey(OrmConsts.IGNORE_NULLS) && (boolean) params.get(OrmConsts.IGNORE_NULLS);
    }


}
