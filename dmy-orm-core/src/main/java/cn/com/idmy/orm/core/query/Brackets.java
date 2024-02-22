package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.constant.SqlConnector;
import cn.com.idmy.orm.core.constant.SqlConsts;
import cn.com.idmy.orm.core.dialect.Dialect;
import cn.com.idmy.orm.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;

import java.util.List;

/**
 * 括号
 */
@Getter
public class Brackets extends QueryCondition {

    private QueryCondition childCondition;

    public Brackets(QueryCondition childCondition) {
        this.childCondition = childCondition;
    }

    @Override
    public QueryColumn getColumn() {
        return childCondition.getColumn();
    }

    @Override
    public void setColumn(QueryColumn column) {
        childCondition.setColumn(column);
    }

    @Override
    public void setValue(Object value) {
        childCondition.setValue(value);
    }

    @Override
    public String getLogic() {
        return childCondition.getLogic();
    }

    @Override
    public void setLogic(String logic) {
        childCondition.setLogic(logic);
    }

    @Override
    protected QueryCondition getNextEffectiveCondition() {
        return childCondition.getNextEffectiveCondition();
    }

    @Override
    public QueryCondition and(QueryCondition nextCondition) {
        connectToChild(nextCondition, SqlConnector.AND);
        return this;
    }

    @Override
    public QueryCondition or(QueryCondition nextCondition) {
        connectToChild(nextCondition, SqlConnector.OR);
        return this;
    }

    protected void connectToChild(QueryCondition nextCondition, SqlConnector connector) {
        childCondition.connect(nextCondition, connector);
    }

    @Override
    public Object getValue() {
        return checkEffective() ? WrapperUtil.getValues(childCondition) : null;
    }

    @Override
    public boolean checkEffective() {
        boolean effective = super.checkEffective();
        if (!effective) {
            return false;
        }
        QueryCondition condition = this.childCondition;
        while (condition != null) {
            if (condition.checkEffective()) {
                return true;
            }
            condition = condition.next;
        }
        return false;
    }

    @Override
    public String toSql(List<QueryTable> queryTables, Dialect dialect) {

        String sqlNext = next == null ? null : next.toSql(queryTables, dialect);

        StringBuilder sql = new StringBuilder();
        if (checkEffective()) {
            String childSql = childCondition.toSql(queryTables, dialect);
            if (StrUtil.isNotBlank(childSql)) {
                QueryCondition prevEffectiveCondition = getPrevEffectiveCondition();
                if (prevEffectiveCondition != null && this.connector != null) {
                    childSql = this.connector + SqlConsts.BRACKET_LEFT + childSql + SqlConsts.BRACKET_RIGHT;
                } else if (StrUtil.isNotBlank(sqlNext)) {
                    childSql = SqlConsts.BRACKET_LEFT + childSql + SqlConsts.BRACKET_RIGHT;
                }
                sql.append(childSql);
            } else {
                //all child conditions are not effective
                //fixed gitee #I6W89G
                this.effective = false;
            }
        }

        return sqlNext != null ? sql + sqlNext : sql.toString();
    }


    @Override
    boolean containsTable(String... tables) {
        if (childCondition != null && childCondition.containsTable(tables)) {
            return true;
        }
        return nextContainsTable(tables);
    }

    @Override
    public String toString() {
        return "Brackets{" +
                "childCondition=" + childCondition +
                '}';
    }

    @Override
    public Brackets clone() {
        Brackets clone = (Brackets) super.clone();
        // deep clone ...
        clone.childCondition = ObjectUtil.clone(this.childCondition);
        return clone;
    }

}
