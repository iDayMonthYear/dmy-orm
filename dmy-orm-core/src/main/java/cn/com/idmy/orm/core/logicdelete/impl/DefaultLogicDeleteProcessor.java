package cn.com.idmy.orm.core.logicdelete.impl;

import cn.com.idmy.orm.core.OrmGlobalConfig;
import cn.com.idmy.orm.core.dialect.Dialect;
import cn.com.idmy.orm.core.logicdelete.AbstractLogicDeleteProcessor;
import cn.com.idmy.orm.core.table.TableInfo;

import static cn.com.idmy.orm.core.constant.SqlConsts.EQUALS;
import static cn.com.idmy.orm.core.constant.SqlConsts.SINGLE_QUOTE;

/**
 * 默认逻辑删除处理器。
 *
 * @author michael
 */
public class DefaultLogicDeleteProcessor extends AbstractLogicDeleteProcessor {

    @Override
    public String buildLogicNormalCondition(String logicColumn, TableInfo tableInfo, Dialect dialect) {
        return dialect.wrap(logicColumn) + EQUALS + prepareValue(getLogicNormalValue());
    }

    @Override
    public String buildLogicDeletedSet(String logicColumn, TableInfo tableInfo, Dialect dialect) {
        return dialect.wrap(logicColumn) + EQUALS + prepareValue(getLogicDeletedValue());
    }

    @Override
    public Object getLogicNormalValue() {
        return OrmGlobalConfig.getDefaultConfig().getNormalValueOfLogicDelete();
    }

    @Override
    public Object getLogicDeletedValue() {
        return OrmGlobalConfig.getDefaultConfig().getDeletedValueOfLogicDelete();
    }

    private static Object prepareValue(Object value) {
        if (value instanceof Number || value instanceof Boolean) {
            return value;
        }
        return SINGLE_QUOTE + value + SINGLE_QUOTE;
    }

}


