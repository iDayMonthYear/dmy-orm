package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.constant.SqlConsts;
import cn.com.idmy.orm.core.dialect.Dialect;
import cn.com.idmy.orm.core.exception.OrmExceptions;
import cn.com.idmy.orm.core.util.ObjectUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UnionWrapper implements CloneSupport<UnionWrapper> {

    private String key;
    private QueryWrapper queryWrapper;

    static UnionWrapper union(QueryWrapper queryWrapper) {
        UnionWrapper unionWrapper = new UnionWrapper();
        unionWrapper.key = SqlConsts.UNION;
        unionWrapper.queryWrapper = queryWrapper;
        return unionWrapper;
    }

    static UnionWrapper unionAll(QueryWrapper queryWrapper) {
        UnionWrapper unionWrapper = new UnionWrapper();
        unionWrapper.key = SqlConsts.UNION_ALL;
        unionWrapper.queryWrapper = queryWrapper;
        return unionWrapper;
    }


    private UnionWrapper() {
    }

    public void buildSql(StringBuilder sqlBuilder, Dialect dialect) {
        sqlBuilder.append(key)
                .append(SqlConsts.BRACKET_LEFT)
                .append(dialect.buildSelectSql(queryWrapper))
                .append(SqlConsts.BRACKET_RIGHT);
    }

    @Override
    public UnionWrapper clone() {
        try {
            UnionWrapper clone = (UnionWrapper) super.clone();
            // deep clone ...
            clone.queryWrapper = ObjectUtil.clone(this.queryWrapper);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw OrmExceptions.wrap(e);
        }
    }

}
