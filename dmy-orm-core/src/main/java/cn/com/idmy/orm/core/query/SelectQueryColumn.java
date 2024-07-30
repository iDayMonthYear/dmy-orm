package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.dialect.Dialect;
import cn.com.idmy.orm.core.util.ObjectUtil;
import cn.com.idmy.orm.core.util.StringUtil;

import java.util.List;

public class SelectQueryColumn extends QueryColumn implements HasParamsColumn {

    private QueryWrapper queryWrapper;

    public SelectQueryColumn(QueryWrapper queryWrapper) {
        this.queryWrapper = queryWrapper;
    }


    QueryWrapper getQueryWrapper() {
        return queryWrapper;
    }

    @Override
    String toSelectSql(List<QueryTable> queryTables, Dialect dialect) {
        String selectSql = dialect.forSelectByQuery(queryWrapper);
        if (StringUtil.isNotBlank(selectSql) && StringUtil.isNotBlank(alias)) {
            selectSql = WrapperUtil.withAlias(selectSql, alias, dialect);
        }
        return selectSql;
    }

    @Override
    public SelectQueryColumn clone() {
        SelectQueryColumn clone = (SelectQueryColumn) super.clone();
        // deep clone ...
        clone.queryWrapper = ObjectUtil.clone(this.queryWrapper);
        return clone;
    }

    @Override
    String toConditionSql(List<QueryTable> queryTables, Dialect dialect) {
        return super.toConditionSql(queryTables, dialect);
    }

    @Override
    public Object[] getParamValues() {
        return queryWrapper.getAllValueArray();
    }

}
