package cn.com.idmy.orm.core.query;


import cn.com.idmy.orm.core.dialect.Dialect;
import cn.com.idmy.orm.core.util.SqlUtil;
import lombok.Getter;

import java.util.List;

/**
 * 原生排序字段。
 *
 * @author michael
 * @author 王帅
 */
@Getter
public class RawQueryOrderBy extends QueryOrderBy {

    protected String content;

    public RawQueryOrderBy(String content) {
        this(content, true);
    }

    public RawQueryOrderBy(String content, boolean checkAvailable) {
        if (checkAvailable) {
            SqlUtil.keepOrderBySqlSafely(content);
        }
        this.content = content;
    }

    @Override
    public String toSql(List<QueryTable> queryTables, Dialect dialect) {
        return content;
    }

    @Override
    public String toString() {
        return "RawQueryOrderBy{" +
                "content='" + content + '\'' +
                '}';
    }

    @Override
    public RawQueryOrderBy clone() {
        return (RawQueryOrderBy) super.clone();
    }

}
