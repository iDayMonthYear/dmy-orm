package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.OrmConsts;
import cn.com.idmy.orm.core.dialect.Dialect;

import java.util.List;

/**
 * 取相反数（{@code -column}）。
 *
 * @author 王帅
 * @since 2023-11-09
 */
public class NegativeQueryColumn extends QueryColumn implements HasParamsColumn {

    private final QueryColumn queryColumn;

    public NegativeQueryColumn(QueryColumn queryColumn) {
        this.queryColumn = queryColumn;
    }

    @Override
    public Object[] getParamValues() {
        if (queryColumn instanceof HasParamsColumn) {
            return ((HasParamsColumn) queryColumn).getParamValues();
        }
        return OrmConsts.EMPTY_ARRAY;
    }

    @Override
    String toSelectSql(List<QueryTable> queryTables, Dialect dialect) {
        return toConditionSql(queryTables, dialect) + WrapperUtil.buildColumnAlias(alias, dialect);
    }

    @Override
    String toConditionSql(List<QueryTable> queryTables, Dialect dialect) {
        return "-" + queryColumn.toConditionSql(queryTables, dialect);
    }

}
