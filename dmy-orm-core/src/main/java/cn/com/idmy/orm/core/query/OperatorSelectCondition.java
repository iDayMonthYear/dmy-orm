package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.constant.SqlConsts;
import cn.com.idmy.orm.core.dialect.Dialect;
import cn.com.idmy.orm.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;

import java.util.List;

/**
 * 操作类型的操作
 * 示例1：and EXISTS (select 1 from ... where ....)
 * 示例2：and not EXISTS (select ... from ... where ....)
 */
public class OperatorSelectCondition extends QueryCondition {

    //操作符，例如 exist, not exist
    private final String operator;
    @Getter
    private QueryWrapper queryWrapper;

    public OperatorSelectCondition(String operator, QueryWrapper queryWrapper) {
        this.operator = operator;
        this.queryWrapper = queryWrapper;
    }

    @Override
    public String toSql(List<QueryTable> queryTables, Dialect dialect) {
        StringBuilder sql = new StringBuilder();

        //检测是否生效
        if (checkEffective()) {
            String childSql = dialect.buildSelectSql(queryWrapper);
            if (StrUtil.isNotBlank(childSql)) {
                QueryCondition prevEffectiveCondition = getPrevEffectiveCondition();
                if (prevEffectiveCondition != null && this.connector != null) {
                    sql.append(this.connector);
                }
                sql.append(operator)
                        .append(SqlConsts.BRACKET_LEFT)
                        .append(childSql)
                        .append(SqlConsts.BRACKET_RIGHT);
            }
        }

        if (this.next != null) {
            return sql + next.toSql(queryTables, dialect);
        }

        return sql.toString();
    }

    @Override
    public Object getValue() {
        return checkEffective() ? queryWrapper.getAllValueArray() : null;
    }

    @Override
    boolean containsTable(String... tables) {
        QueryCondition condition = queryWrapper.getWhereQueryCondition();
        return condition != null && condition.containsTable(tables);
    }

    @Override
    public OperatorSelectCondition clone() {
        OperatorSelectCondition clone = (OperatorSelectCondition) super.clone();
        // deep clone ...
        clone.queryWrapper = ObjectUtil.clone(this.queryWrapper);
        return clone;
    }

}
