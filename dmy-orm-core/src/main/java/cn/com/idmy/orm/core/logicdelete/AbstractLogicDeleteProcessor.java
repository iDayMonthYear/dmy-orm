package cn.com.idmy.orm.core.logicdelete;

import cn.com.idmy.orm.core.dialect.Dialect;
import cn.com.idmy.orm.core.query.QueryColumn;
import cn.com.idmy.orm.core.query.QueryTable;
import cn.com.idmy.orm.core.query.QueryWrapper;
import cn.com.idmy.orm.core.table.TableInfo;

import static cn.com.idmy.orm.core.constant.SqlConsts.EQUALS;

/**
 * 逻辑删除处理器抽象类。
 *
 * @author 王帅
 * @since 2023-06-20
 */
public abstract class AbstractLogicDeleteProcessor implements LogicDeleteProcessor {

    @Override
    public String buildLogicNormalCondition(String logicColumn, TableInfo tableInfo, Dialect dialect) {
        return dialect.wrap(logicColumn) + EQUALS + getLogicNormalValue();
    }

    @Override
    public String buildLogicDeletedSet(String logicColumn, TableInfo tableInfo, Dialect dialect) {
        return dialect.wrap(logicColumn) + EQUALS + getLogicDeletedValue();
    }

    @Override
    public void buildQueryCondition(QueryWrapper queryWrapper, TableInfo tableInfo, String joinTableAlias) {
        QueryTable queryTable = new QueryTable(tableInfo.getSchema(), tableInfo.getTableName()).as(joinTableAlias);
        QueryColumn queryColumn = new QueryColumn(queryTable, tableInfo.getLogicDeleteColumn());
        queryWrapper.and(queryColumn.eq(getLogicNormalValue()));
    }

}


