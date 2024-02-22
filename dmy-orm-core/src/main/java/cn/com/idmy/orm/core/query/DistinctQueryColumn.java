package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.constant.SqlConsts;
import cn.com.idmy.orm.core.dialect.Dialect;
import cn.com.idmy.orm.core.util.CollectionUtil;
import cn.com.idmy.orm.core.util.StringUtil;

import java.util.List;

public class DistinctQueryColumn extends QueryColumn {

    private List<QueryColumn> queryColumns;

    public DistinctQueryColumn(QueryColumn... queryColumns) {
        this.queryColumns = CollectionUtil.newArrayList(queryColumns);
    }

    @Override
    public String toSelectSql(List<QueryTable> queryTables, Dialect dialect) {
        if (CollectionUtil.isEmpty(queryTables)) {
            return SqlConsts.EMPTY;
        }

        String sql = SqlConsts.DISTINCT + StringUtil.join(SqlConsts.DELIMITER, queryColumns, queryColumn ->
                queryColumn.toSelectSql(queryTables, dialect));

        return sql + WrapperUtil.buildColumnAlias(alias, dialect);
    }


    @Override
    String toConditionSql(List<QueryTable> queryTables, Dialect dialect) {
        if (CollectionUtil.isEmpty(queryTables)) {
            return SqlConsts.EMPTY;
        }

        return SqlConsts.DISTINCT + StringUtil.join(SqlConsts.DELIMITER, queryColumns, queryColumn ->
                queryColumn.toSelectSql(queryTables, dialect));

    }

    @Override
    public DistinctQueryColumn clone() {
        DistinctQueryColumn clone = (DistinctQueryColumn) super.clone();
        clone.queryColumns = CollectionUtil.cloneArrayList(this.queryColumns);
        return clone;
    }

}
