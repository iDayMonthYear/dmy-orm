package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.constant.SqlConsts;
import cn.com.idmy.orm.core.dialect.Dialect;
import cn.com.idmy.orm.core.util.CollectionUtil;
import cn.com.idmy.orm.core.util.SqlUtil;
import cn.com.idmy.orm.core.util.StringUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

/**
 * 数据库 聚合函数，例如 CONVERT(NVARCHAR(30), GETDATE(), 126) 等等
 */
@Setter
@Getter
public class StringFunctionQueryColumn extends QueryColumn {

    protected String fnName;
    protected List<String> params;

    public StringFunctionQueryColumn(String fnName, String... params) {
        SqlUtil.keepColumnSafely(fnName);
        this.fnName = fnName;
        this.params = Arrays.asList(params);
    }


    @Override
    public String toSelectSql(List<QueryTable> queryTables, Dialect dialect) {
        String sql = StringUtil.join(SqlConsts.DELIMITER, params);
        if (StringUtil.isBlank(sql)) {
            return SqlConsts.EMPTY;
        }
        if (StringUtil.isBlank(alias)) {
            return fnName + WrapperUtil.withBracket(sql);
        }
        return fnName + WrapperUtil.withAlias(sql, alias, dialect);
    }

    @Override
    String toConditionSql(List<QueryTable> queryTables, Dialect dialect) {
        String sql = StringUtil.join(SqlConsts.DELIMITER, params);
        if (StringUtil.isBlank(sql)) {
            return SqlConsts.EMPTY;
        }
        return fnName + WrapperUtil.withBracket(sql);
    }


    @Override
    public String toString() {
        return "StringFunctionQueryColumn{" +
                "fnName='" + fnName + '\'' +
                ", params=" + params +
                '}';
    }

    @Override
    public StringFunctionQueryColumn clone() {
        StringFunctionQueryColumn clone = (StringFunctionQueryColumn) super.clone();
        // deep clone ...
        clone.params = CollectionUtil.newArrayList(this.params);
        return clone;
    }

}
