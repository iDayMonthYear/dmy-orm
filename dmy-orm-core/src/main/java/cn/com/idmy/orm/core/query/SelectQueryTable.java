package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.dialect.Dialect;
import cn.com.idmy.orm.core.dialect.OperateType;
import cn.com.idmy.orm.core.util.StringUtil;
import lombok.Getter;

/**
 * 查询的 table，
 * 实例1：用于构建 select * from (select ...) 中的第二个 select
 * 实例2：用于构建 left join (select ...) 中的 select
 */
@Getter
public class SelectQueryTable extends QueryTable {

    private QueryWrapper queryWrapper;

    public SelectQueryTable(QueryWrapper queryWrapper) {
        super();
        this.queryWrapper = queryWrapper;
    }

    public void setQueryWrapper(QueryWrapper queryWrapper) {
        this.queryWrapper = queryWrapper;
    }

    @Override
    Object[] getValueArray() {
        return queryWrapper.getAllValueArray();
    }

    @Override
    public String toSql(Dialect dialect, OperateType operateType) {
        String sql = dialect.buildSelectSql(queryWrapper);
        if (StringUtil.isNotBlank(alias)) {
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

    @Override
    public String toString() {
        return queryWrapper.toSQL();
    }
}
