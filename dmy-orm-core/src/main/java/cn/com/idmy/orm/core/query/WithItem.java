package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.dialect.Dialect;
import cn.com.idmy.orm.core.exception.OrmExceptions;
import cn.com.idmy.orm.core.util.CollectionUtil;
import cn.com.idmy.orm.core.util.ObjectUtil;
import cn.com.idmy.orm.core.util.StringUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static cn.com.idmy.orm.core.constant.SqlConsts.*;

@Setter
@Getter
public class WithItem implements CloneSupport<WithItem> {
    private String name;
    private List<String> params;
    private WithDetail withDetail;

    public WithItem() {
    }

    public WithItem(String name, List<String> params) {
        this.name = name;
        this.params = params;
    }

    public String toSql(Dialect dialect) {
        StringBuilder sql = new StringBuilder(name);
        if (CollectionUtil.isNotEmpty(params)) {
            sql.append(BRACKET_LEFT).append(StringUtil.join(DELIMITER, params)).append(BRACKET_RIGHT);
        }
        sql.append(AS).append(BRACKET_LEFT);
        sql.append(withDetail.toSql(dialect));
        return sql.append(BRACKET_RIGHT).toString();
    }

    public Object[] getParamValues() {
        return withDetail.getParamValues();
    }

    @Override
    public WithItem clone() {
        try {
            WithItem clone = (WithItem) super.clone();
            // deep clone ...
            clone.withDetail = ObjectUtil.clone(this.withDetail);
            clone.params = CollectionUtil.newArrayList(this.params);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw OrmExceptions.wrap(e);
        }
    }

}
