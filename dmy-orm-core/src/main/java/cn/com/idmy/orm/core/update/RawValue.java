package cn.com.idmy.orm.core.update;

import cn.com.idmy.orm.core.constant.SqlConsts;
import cn.com.idmy.orm.core.dialect.Dialect;
import cn.com.idmy.orm.core.query.CPI;
import cn.com.idmy.orm.core.query.QueryColumn;
import cn.com.idmy.orm.core.query.QueryCondition;
import cn.com.idmy.orm.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * @author michael
 */
@RequiredArgsConstructor
public class RawValue implements Serializable {
    private final Object object;

    public String toSql(Dialect dialect) {
        if (object instanceof String) {
            return (String) object;
        }

        if (object instanceof QueryWrapper obj) {
            return SqlConsts.BRACKET_LEFT + dialect.buildSelectSql(obj) + SqlConsts.BRACKET_RIGHT;
        }

        if (object instanceof QueryCondition obj) {
            return obj.toSql(null, dialect);
        }

        if (object instanceof QueryColumn obj) {
            return CPI.toSelectSql(obj, null, dialect);
        }

        return object.toString();
    }
}
