package cn.com.idmy.orm.core.table;

import cn.com.idmy.orm.core.query.QueryTable;
import cn.com.idmy.orm.core.util.MapUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 表定义，内包含字段。
 *
 * @author 王帅
 * @since 2024-03-11
 */
public class TableDef extends QueryTable {

    private static final Map<String, TableDef> CACHE = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    protected static <V extends TableDef> V getCache(String key, Function<String, V> mappingFunction) {
        return MapUtil.computeIfAbsent((Map<String, V>) CACHE, key, mappingFunction);
    }

    protected TableDef(String schema, String tableName) {
        super(schema, tableName);
    }

    protected TableDef(String schema, String tableName, String alias) {
        super(schema, tableName, alias);
    }

    /**
     * 兼容方法，与 {@link #getName()} 相同。
     *
     * @return 表名
     */
    public String getTableName() {
        return name;
    }

    public TableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new TableDef(this.schema, this.name, alias));
    }
}
