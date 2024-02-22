package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.OrmConsts;
import cn.com.idmy.orm.core.dialect.Dialect;
import cn.com.idmy.orm.core.exception.OrmExceptions;
import cn.com.idmy.orm.core.table.TableDef;
import cn.com.idmy.orm.core.util.StringUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.util.Objects;

/**
 * 查询列，描述的是一张表的字段
 */
@Data
public class QueryTable implements CloneSupport<QueryTable> {

    protected int tableDefHashCode = 0;
    protected String schema;
    protected String name;
    protected String alias;

    public QueryTable() {
    }

    public QueryTable(TableDef tableDef) {
        // TableDef的标识符号,0:不确定标识
        this.tableDefHashCode = tableDef.hashCode();
        this.schema = tableDef.getSchema();
        this.name = tableDef.getTable();
    }

    public QueryTable(String name) {
        String[] schemaAndTableName = StringUtil.getSchemaAndTableName(name);
        this.schema = schemaAndTableName[0];
        this.name = schemaAndTableName[1];
    }

    public QueryTable(String schema, String name) {
        this.schema = StrUtil.trim(schema);
        this.name = StrUtil.trim(name);
    }

    public QueryTable(String schema, String table, String alias) {
        this.schema = StrUtil.trim(schema);
        this.name = StrUtil.trim(table);
        this.alias = StrUtil.trim(alias);
    }

    public String getNameWithSchema() {
        return StrUtil.isNotBlank(schema) ? schema + "." + name : name;
    }

    public QueryTable as(String alias) {
        this.alias = alias;
        return this;
    }

    boolean isSameTable(QueryTable table) {
        if (table == null) {
            return false;
        }
        if (StrUtil.isNotBlank(alias) && StrUtil.isNotBlank(table.alias) && (Objects.equals(alias, table.alias))) {
            return false;
        }
        //比较对象都有tableDef标记,就用标记比对, 否则就用名称比对
        if (tableDefHashCode != 0 && table.tableDefHashCode != 0) {
            return tableDefHashCode == table.tableDefHashCode;
        }
        return Objects.equals(name, table.name);
    }

    Object[] getValueArray() {
        return OrmConsts.EMPTY_ARRAY;
    }

    public String toSql(Dialect dialect) {
        String sql;
        if (StrUtil.isNotBlank(schema)) {
            String table = dialect.getRealTable(name);
            sql = dialect.wrap(dialect.getRealSchema(schema, table)) + "." + dialect.wrap(table) + WrapperUtil.buildAlias(alias, dialect);
        } else {
            sql = dialect.wrap(dialect.getRealTable(name)) + WrapperUtil.buildAlias(alias, dialect);
        }
        return sql;
    }

    @Override
    public String toString() {
        return "QueryTable{" + "schema='" + schema + '\'' + ", name='" + name + '\'' + ", alias='" + alias + '\'' + '}';
    }

    @Override
    public QueryTable clone() {
        try {
            return (QueryTable) super.clone();
        } catch (CloneNotSupportedException e) {
            throw OrmExceptions.wrap(e);
        }
    }
}
