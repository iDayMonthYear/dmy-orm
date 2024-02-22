package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.constant.SqlConsts;
import cn.com.idmy.orm.core.dialect.Dialect;
import cn.com.idmy.orm.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;

import java.util.List;

/**
 * 操作类型的操作
 * 示例1：and not ( id > 100 and name like %%)
 */
public class OperatorQueryCondition extends QueryCondition {

    private final String operator;
    @Getter
    private QueryCondition childCondition;

    public OperatorQueryCondition(String operator, QueryCondition childCondition) {
        this.operator = operator;
        this.childCondition = childCondition;
    }

    @Override
    public String toSql(List<QueryTable> queryTables, Dialect dialect) {
        StringBuilder sql = new StringBuilder();

        //检测是否生效
        if (checkEffective()) {
            String childSql = childCondition.toSql(queryTables, dialect);
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
        return checkEffective() ? WrapperUtil.getValues(childCondition) : null;
    }

    @Override
    boolean containsTable(String... tables) {
        if (childCondition != null && childCondition.containsTable(tables)) {
            return true;
        }
        return nextContainsTable(tables);
    }

    @Override
    public OperatorQueryCondition clone() {
        OperatorQueryCondition clone = (OperatorQueryCondition) super.clone();
        clone.childCondition = ObjectUtil.clone(this.childCondition);
        return clone;
    }

}
