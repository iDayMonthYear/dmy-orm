package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.dialect.Dialect;
import cn.com.idmy.orm.core.exception.OrmExceptions;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WithSelectDetail implements WithDetail {

    private QueryWrapper queryWrapper;

    public WithSelectDetail() {
    }

    public WithSelectDetail(QueryWrapper queryWrapper) {
        this.queryWrapper = queryWrapper;
    }

    @Override
    public String toSql(Dialect dialect) {
        return dialect.buildSelectSql(queryWrapper);
    }

    @Override
    public Object[] getParamValues() {
        return queryWrapper.getAllValueArray();
    }

    @Override
    public WithSelectDetail clone() {
        try {
            WithSelectDetail clone = (WithSelectDetail) super.clone();
            // deep clone ...
            clone.queryWrapper = this.queryWrapper.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw OrmExceptions.wrap(e);
        }
    }

}
