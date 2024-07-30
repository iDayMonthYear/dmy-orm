package cn.com.idmy.orm.core.update;

import cn.com.idmy.orm.core.constant.SqlConsts;
import cn.com.idmy.orm.core.dialect.Dialect;
import cn.com.idmy.orm.core.query.*;

import java.io.Serializable;

/**
 * @author michael
 */
public class RawValue implements Serializable {

    private final Object object;

    public RawValue(Object object) {
        this.object = object;
    }

    public String toSql(Dialect dialect) {
        if (object instanceof String) {
            return (String) object;
        }

        if (object instanceof QueryWrapper) {
            return SqlConsts.BRACKET_LEFT + dialect.buildSelectSql((QueryWrapper) object) + SqlConsts.BRACKET_RIGHT;
        }

        if (object instanceof QueryCondition) {
            return ((QueryCondition) object).toSql(null, dialect);
        }

        if (object instanceof QueryColumn) {
            return CPI.toSelectSql((QueryColumn) object, null, dialect);
        }

        return object.toString();
    }

    public Object[] getParams() {
        if (object instanceof String) {
            return new Object[0];
        }

        if (object instanceof QueryWrapper) {
            return CPI.getValueArray((QueryWrapper) object);
        }

        if (object instanceof QueryCondition) {
            return CPI.getConditionParams((QueryCondition) object);
        }

        if (object instanceof HasParamsColumn) {
            return ((HasParamsColumn) object).getParamValues();
        }

        return new Object[0];
    }

}
