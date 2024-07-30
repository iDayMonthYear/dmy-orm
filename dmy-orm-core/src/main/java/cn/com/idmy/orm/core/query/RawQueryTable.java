package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.dialect.Dialect;
import cn.com.idmy.orm.core.dialect.OperateType;
import cn.com.idmy.orm.core.util.StringUtil;
import lombok.Getter;

import java.util.Objects;

/**
 * 原生查询表。
 *
 * @author 王帅
 * @since 2023-10-16
 */
@Getter
public class RawQueryTable extends QueryTable {

    protected String content;

    public RawQueryTable(String content) {
        this.content = content;
    }

    @Override
    public String toSql(Dialect dialect, OperateType operateType) {
        return this.content + WrapperUtil.buildAlias(alias, dialect);
    }

    @Override
    boolean isSameTable(QueryTable table) {
        if (table == null) {
            return false;
        }
        // 只比较别名，不比较内容
        if (StringUtil.isNotBlank(alias)
                && StringUtil.isNotBlank(table.alias)) {
            return Objects.equals(alias, table.alias);
        }
        return false;
    }

    @Override
    public String toString() {
        return "RawQueryTable{" +
                "content='" + content + '\'' +
                '}';
    }

    @Override
    public RawQueryTable clone() {
        return (RawQueryTable) super.clone();
    }

}
