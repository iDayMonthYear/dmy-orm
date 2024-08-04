package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.OrmConsts;
import cn.com.idmy.orm.core.dialect.Dialect;
import cn.com.idmy.orm.core.dialect.OperateType;
import cn.com.idmy.orm.core.exception.OrmExceptions;
import cn.com.idmy.orm.core.util.StringUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * 查询表。
 *
 * @author michael
 * @author 王帅
 */
@Setter
@Getter
public class QueryTable implements CloneSupport<QueryTable> {
    protected String schema;
    protected String name;
    protected String alias;

    protected QueryTable() {
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
        if (this == table) {
            return true;
        }
        if (StrUtil.isNotBlank(alias) && StrUtil.isNotBlank(table.alias)) {
            return Objects.equals(alias, table.alias);
        } else {
            return Objects.equals(name, table.name);
        }
    }

    Object[] getValueArray() {
        return OrmConsts.EMPTY_ARRAY;
    }

    public String toSql(Dialect dialect, OperateType operateType) {
        if (StrUtil.isBlank(schema)) {
            return dialect.wrap(dialect.getRealTable(name, operateType)) + WrapperUtil.buildAlias(alias, dialect);
        } else {
            String table = dialect.getRealTable(name, operateType);
            return dialect.wrap(dialect.getRealSchema(schema, table, operateType)) + "." + dialect.wrap(table) + WrapperUtil.buildAlias(alias, dialect);
        }
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
