package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.dialect.Dialect;
import cn.hutool.core.util.ArrayUtil;

import java.util.List;

/**
 * IF 函数查询列。
 *
 * @author 王帅
 * @since 2023-07-07
 */
public class IfFunctionQueryColumn extends QueryColumn implements HasParamsColumn {

    private QueryCondition condition;
    private QueryColumn trueValue;
    private QueryColumn falseValue;

    public IfFunctionQueryColumn(QueryCondition condition, QueryColumn trueValue, QueryColumn falseValue) {
        this.condition = condition;
        this.trueValue = trueValue;
        this.falseValue = falseValue;
    }

    @Override
    String toConditionSql(List<QueryTable> queryTables, Dialect dialect) {
        return "IF(" + condition.toSql(queryTables, dialect) + ", " +
                trueValue.toConditionSql(queryTables, dialect) + ", " +
                falseValue.toConditionSql(queryTables, dialect) + ")";
    }

    @Override
    public Object[] getParamValues() {
        Object[] params = WrapperUtil.getValues(condition);
        // IF 函数嵌套
        if (trueValue instanceof HasParamsColumn) {
            Object[] paramValues = ((HasParamsColumn) trueValue).getParamValues();
            params = ArrayUtil.addAll(params, paramValues);
        }
        if (falseValue instanceof HasParamsColumn) {
            Object[] paramValues = ((HasParamsColumn) falseValue).getParamValues();
            params = ArrayUtil.addAll(params, paramValues);
        }
        return params;
    }

    @Override
    public IfFunctionQueryColumn clone() {
        IfFunctionQueryColumn clone = (IfFunctionQueryColumn) super.clone();
        // deep clone ...
        clone.condition = this.condition.clone();
        clone.trueValue = this.trueValue.clone();
        clone.falseValue = this.falseValue.clone();
        return clone;
    }

}
