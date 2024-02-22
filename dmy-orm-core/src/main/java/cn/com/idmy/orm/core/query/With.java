package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.OrmConsts;
import cn.com.idmy.orm.core.dialect.Dialect;
import cn.com.idmy.orm.core.exception.OrmExceptions;
import cn.com.idmy.orm.core.util.ArrayUtil;
import cn.com.idmy.orm.core.util.CollectionUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static cn.com.idmy.orm.core.constant.SqlConsts.*;

@Setter
@Getter
public class With implements CloneSupport<With> {

    private boolean recursive;
    private List<WithItem> withItems;

    public With() {
    }

    public With(boolean recursive) {
        this.recursive = recursive;
    }

    public void addWithItem(WithItem withItem) {
        if (withItems == null) {
            withItems = new ArrayList<>();
        }
        withItems.add(withItem);
    }

    public String toSql(Dialect dialect) {
        StringBuilder sql = new StringBuilder(WITH);
        if (recursive) {
            sql.append(RECURSIVE);
        }
        for (int i = 0; i < withItems.size(); i++) {
            sql.append(withItems.get(i).toSql(dialect));
            if (i != withItems.size() - 1) {
                sql.append(DELIMITER);
            }
        }
        return sql.append(BLANK).toString();
    }

    public Object[] getParamValues() {
        Object[] paramValues = OrmConsts.EMPTY_ARRAY;
        for (WithItem withItem : withItems) {
            paramValues = ArrayUtil.concat(paramValues, withItem.getParamValues());
        }
        return paramValues;
    }

    @Override
    public With clone() {
        try {
            With clone = (With) super.clone();
            // deep clone ...
            clone.withItems = CollectionUtil.cloneArrayList(this.withItems);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw OrmExceptions.wrap(e);
        }
    }

}
