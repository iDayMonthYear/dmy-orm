package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.dialect.Dialect;
import cn.com.idmy.orm.core.exception.OrmExceptions;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WithStringDetail implements WithDetail {

    private String rawSQL;
    private Object[] params;

    public WithStringDetail(String rawSQL, Object[] params) {
        this.rawSQL = rawSQL;
        this.params = params;
    }

    @Override
    public String toSql(Dialect dialect) {
        return rawSQL;
    }

    @Override
    public Object[] getParamValues() {
        return params;
    }

    @Override
    public WithStringDetail clone() {
        try {
            return (WithStringDetail) super.clone();
        } catch (CloneNotSupportedException e) {
            throw OrmExceptions.wrap(e);
        }
    }

}
