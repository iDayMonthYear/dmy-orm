package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.dialect.Dialect;
import cn.com.idmy.orm.core.exception.OrmExceptions;
import cn.com.idmy.orm.core.util.CollectionUtil;
import cn.com.idmy.orm.core.util.StringUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class WithValuesDetail implements WithDetail {

    private List<Object> values;
    @Setter
    @Getter
    private QueryWrapper queryWrapper;

    public WithValuesDetail() {
    }

    public WithValuesDetail(List<Object> values, QueryWrapper queryWrapper) {
        this.values = values;
        this.queryWrapper = queryWrapper;
    }

    @Override
    public String toSql(Dialect dialect) {
        List<String> stringValues = new ArrayList<>(values.size());
        for (Object value : values) {
            stringValues.add(String.valueOf(value));
        }
        StringBuilder sql = new StringBuilder("VALUES (")
                .append(StringUtil.join(", ", stringValues)).append(") ");
        return sql.append(dialect.buildNoSelectSql(queryWrapper)).toString();
    }

    @Override
    public Object[] getParamValues() {
        return queryWrapper.getAllValueArray();
    }

    @Override
    public WithValuesDetail clone() {
        try {
            WithValuesDetail clone = (WithValuesDetail) super.clone();
            // deep clone ...
            clone.values = CollectionUtil.newArrayList(this.values);
            clone.queryWrapper = this.queryWrapper.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw OrmExceptions.wrap(e);
        }
    }

}
