package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.dialect.Dialect;

public interface WithDetail extends CloneSupport<WithDetail> {

    String toSql(Dialect dialect);

    Object[] getParamValues();

}
