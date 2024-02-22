package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.dialect.Dialect;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * 查询的 table，
 * 实例1：用于构建 select * from (select ...) 中的第二个 select
 * 实例2：用于构建 left join (select ...) 中的 select
 */
@Setter
@Getter
public class SelectQueryTable extends QueryTable {

    private QueryWrapper queryWrapper;

    public SelectQueryTable(QueryWrapper queryWrapper) {
        super();
        this.queryWrapper = queryWrapper;
    }

    @Override
    Object[] getValueArray() {
        return queryWrapper.getAllValueArray();
    }

    @Override
    public String toSql(Dialect dialect) {
        String sql = dialect.buildSelectSql(queryWrapper);
        if (StrUtil.isNotBlank(alias)) {
            return WrapperUtil.withAlias(sql, alias, dialect);
        } else {
            return WrapperUtil.withBracket(sql);
        }
    }

    @Override
    public SelectQueryTable clone() {
        SelectQueryTable clone = (SelectQueryTable) super.clone();
        // deep clone ...
        clone.queryWrapper = this.queryWrapper.clone();
        return clone;
    }

}
