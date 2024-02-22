package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.OrmConsts;
import cn.com.idmy.orm.core.constant.SqlConsts;
import cn.com.idmy.orm.core.dialect.Dialect;
import cn.com.idmy.orm.core.exception.OrmExceptions;
import cn.com.idmy.orm.core.util.ArrayUtil;
import cn.com.idmy.orm.core.util.CollectionUtil;
import cn.com.idmy.orm.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class CaseQueryColumn extends QueryColumn implements HasParamsColumn {
    private List<When> whens;
    private Object elseValue;

    void addWhen(When when) {
        if (whens == null) {
            whens = new ArrayList<>();
        }
        whens.add(when);
    }


    @Override
    String toSelectSql(List<QueryTable> queryTables, Dialect dialect) {
        String sql = buildSql(queryTables, dialect);
        if (StrUtil.isNotBlank(alias)) {
            return WrapperUtil.withAlias(sql, alias, dialect);
        } else {
            return sql;
        }
    }

    @Override
    public CaseQueryColumn clone() {
        CaseQueryColumn clone = (CaseQueryColumn) super.clone();
        // deep clone ...
        clone.whens = CollectionUtil.cloneArrayList(this.whens);
        clone.elseValue = ObjectUtil.cloneObject(this.elseValue);
        return clone;
    }


    @Override
    String toConditionSql(List<QueryTable> queryTables, Dialect dialect) {
        return WrapperUtil.withBracket(buildSql(queryTables, dialect));
    }

    private String buildSql(List<QueryTable> queryTables, Dialect dialect) {
        StringBuilder sql = new StringBuilder(SqlConsts.CASE);
        for (When when : whens) {
            sql.append(SqlConsts.WHEN).append(when.whenCondition.toSql(queryTables, dialect));
            sql.append(SqlConsts.THEN).append(WrapperUtil.buildValue(queryTables, when.thenValue));
        }
        if (elseValue != null) {
            sql.append(SqlConsts.ELSE).append(WrapperUtil.buildValue(queryTables, elseValue));
        }
        sql.append(SqlConsts.END);
        return sql.toString();
    }

    @Override
    public Object[] getParamValues() {
        Object[] values = OrmConsts.EMPTY_ARRAY;
        for (When when : whens) {
            values = ArrayUtil.concat(values, WrapperUtil.getValues(when.whenCondition));
        }
        if (elseValue instanceof HasParamsColumn) {
            values = ArrayUtil.concat(values, ((HasParamsColumn) elseValue).getParamValues());
        }
        return values;
    }


    public static class When implements CloneSupport<When> {
        private QueryCondition whenCondition;
        @Setter
        private Object thenValue;

        public When(QueryCondition whenCondition) {
            this.whenCondition = whenCondition;
        }

        @Override
        public When clone() {
            try {
                When clone = (When) super.clone();
                clone.whenCondition = ObjectUtil.clone(this.whenCondition);
                clone.thenValue = ObjectUtil.cloneObject(this.thenValue);
                return clone;
            } catch (CloneNotSupportedException e) {
                throw OrmExceptions.wrap(e);
            }
        }

    }

    public static class Builder {
        private final CaseQueryColumn caseQueryColumn = new CaseQueryColumn();
        private When lastWhen;

        public Then when(QueryCondition condition) {
            lastWhen = new When(condition);
            return new Then(this);
        }

        public Builder else_(Object elseValue) {
            caseQueryColumn.elseValue = elseValue;
            return this;
        }

        public CaseQueryColumn end() {
            return caseQueryColumn;
        }

        @RequiredArgsConstructor
        public static class Then {
            private final Builder builder;

            public Builder then(Object thenValue) {
                this.builder.lastWhen.setThenValue(thenValue);
                this.builder.caseQueryColumn.addWhen(builder.lastWhen);
                return builder;
            }
        }
    }
}
