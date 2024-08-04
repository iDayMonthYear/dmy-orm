package cn.com.idmy.orm.core.query;


import cn.com.idmy.orm.core.constant.SqlConsts;
import cn.com.idmy.orm.core.dialect.Dialect;
import cn.com.idmy.orm.core.exception.OrmExceptions;
import cn.com.idmy.orm.core.util.ObjectUtil;

import java.util.List;

/**
 * 排序字段
 *
 * @author michael
 */
public class QueryOrderBy implements CloneSupport<QueryOrderBy> {
    QueryColumn queryColumn;
    private String orderType = SqlConsts.ASC;

    private boolean nullsFirst = false;
    private boolean nullsLast = false;

    protected QueryOrderBy() {
    }

    public QueryOrderBy(QueryColumn queryColumn, String orderType) {
        if (queryColumn == null) {
            throw new IllegalArgumentException("排序字段为空，请检查字段是否正确");
        }
        this.queryColumn = queryColumn;
        this.orderType = orderType;
    }

    public QueryOrderBy(QueryColumn queryColumn) {
        if (queryColumn == null) {
            throw new IllegalArgumentException("排序字段为空，请检查字段是否正确");
        }
        this.queryColumn = queryColumn;
    }

    public QueryOrderBy nullsFirst() {
        this.nullsFirst = true;
        this.nullsLast = false;
        return this;
    }

    public QueryOrderBy nullsLast() {
        this.nullsFirst = false;
        this.nullsLast = true;
        return this;
    }

    public String toSql(List<QueryTable> queryTables, Dialect dialect) {
        String sql = queryColumn.toConditionSql(queryTables, dialect) + orderType;
        if (nullsFirst) {
            sql = sql + SqlConsts.NULLS_FIRST;
        } else if (nullsLast) {
            sql = sql + SqlConsts.NULLS_LAST;
        }
        return sql;
    }


    @Override
    public QueryOrderBy clone() {
        try {
            QueryOrderBy clone = (QueryOrderBy) super.clone();
            // deep clone ...
            clone.queryColumn = ObjectUtil.clone(this.queryColumn);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw OrmExceptions.wrap(e);
        }
    }

}
