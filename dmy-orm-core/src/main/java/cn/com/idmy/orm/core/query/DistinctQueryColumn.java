package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.OrmConsts;
import cn.com.idmy.orm.core.constant.SqlConsts;
import cn.com.idmy.orm.core.dialect.Dialect;
import cn.com.idmy.orm.core.util.CollectionUtil;
import cn.com.idmy.orm.core.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DistinctQueryColumn extends QueryColumn implements HasParamsColumn {

    private List<QueryColumn> queryColumns;

    public DistinctQueryColumn(QueryColumn... queryColumns) {
        this.queryColumns = CollectionUtil.newArrayList(queryColumns);
    }

    public List<QueryColumn> getQueryColumns() {
        return queryColumns;
    }

    public void setQueryColumns(List<QueryColumn> queryColumns) {
        this.queryColumns = queryColumns;
    }

    @Override
    String toConditionSql(List<QueryTable> queryTables, Dialect dialect) {
        if (CollectionUtil.isEmpty(queryTables)) {
            return SqlConsts.EMPTY;
        }

        return SqlConsts.DISTINCT + StringUtil.join(
                SqlConsts.DELIMITER,
                queryColumns,
                queryColumn -> queryColumn.toSelectSql(queryTables, dialect)
        );
    }

    @Override
    public String toSelectSql(List<QueryTable> queryTables, Dialect dialect) {
        if (CollectionUtil.isEmpty(queryTables)) {
            return SqlConsts.EMPTY;
        }

        String sql = SqlConsts.DISTINCT + StringUtil.join(
                SqlConsts.DELIMITER,
                queryColumns,
                queryColumn -> queryColumn.toSelectSql(queryTables, dialect)
        );

        return sql + WrapperUtil.buildColumnAlias(alias, dialect);
    }

    @Override
    public DistinctQueryColumn clone() {
        DistinctQueryColumn clone = (DistinctQueryColumn) super.clone();
        // deep clone ...
        clone.queryColumns = CollectionUtil.cloneArrayList(this.queryColumns);

        return clone;
    }

    @Override
    public Object[] getParamValues() {
        if (CollectionUtil.isEmpty(queryColumns)) {
            return OrmConsts.EMPTY_ARRAY;
        }

        List<Object> params = new ArrayList<>();

        for (QueryColumn queryColumn : queryColumns) {
            if (queryColumn instanceof HasParamsColumn) {
                Object[] paramValues = ((HasParamsColumn) queryColumn).getParamValues();

                params.addAll(Arrays.asList(paramValues));
            }
        }

        return params.toArray();
    }
}
