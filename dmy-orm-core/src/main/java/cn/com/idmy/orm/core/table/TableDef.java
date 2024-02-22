package cn.com.idmy.orm.core.table;

import cn.com.idmy.orm.core.query.QueryTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * @author michael
 */
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class TableDef implements Serializable {
    private String schema;
    private final String table;

    public QueryTable as(String alias) {
        return new QueryTable(schema, table, alias);
    }
}
