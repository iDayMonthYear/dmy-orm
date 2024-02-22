package cn.com.idmy.orm.core.query;


import cn.com.idmy.orm.core.dialect.Dialect;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 原生列。
 *
 * @author michael
 * @author 王帅
 */
@Getter
public class RawQueryColumn extends QueryColumn implements HasParamsColumn {
    protected String content;
    protected Object[] params;

    public RawQueryColumn(Object content, Object... params) {
        this.content = String.valueOf(content);
        this.params = params;
    }

    @Override
    String toConditionSql(List<QueryTable> queryTables, Dialect dialect) {
        return content;
    }

    @Override
    String toSelectSql(List<QueryTable> queryTables, Dialect dialect) {
        return content + WrapperUtil.buildColumnAlias(alias, dialect);
    }

    @Override
    public String toString() {
        return "RawQueryColumn{" +
                "content='" + content + '\'' +
                ", params='" + Arrays.toString(params) + '\'' +
                '}';
    }

    @Override
    public RawQueryColumn clone() {
        return (RawQueryColumn) super.clone();
    }

    @Override
    public Object[] getParamValues() {
        return params;
    }

}
