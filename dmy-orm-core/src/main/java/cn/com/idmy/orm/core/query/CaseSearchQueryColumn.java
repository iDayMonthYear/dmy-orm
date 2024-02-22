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

public class CaseSearchQueryColumn extends QueryColumn implements HasParamsColumn {

    private QueryColumn queryColumn;
    private List<When> whens;
    private Object elseValue;

    @Override
    String toSelectSql(List<QueryTable> queryTables, Dialect dialect) {
        String sql = buildSql(queryTables, dialect);
        if (StrUtil.isNotBlank(alias)) {
            return WrapperUtil.withAlias(sql, alias, dialect);
        } else {
            return sql;
        }
    }

    private String buildSql(List<QueryTable> queryTables, Dialect dialect) {
        StringBuilder sql = new StringBuilder(SqlConsts.CASE);
        sql.append(SqlConsts.BLANK).append(queryColumn.toSelectSql(queryTables, dialect));
        for (When when : whens) {
            sql.append(SqlConsts.WHEN).append(WrapperUtil.buildValue(queryTables, when.searchValue));
            sql.append(SqlConsts.THEN).append(WrapperUtil.buildValue(queryTables, when.thenValue));
        }
        if (elseValue != null) {
            sql.append(SqlConsts.ELSE).append(WrapperUtil.buildValue(queryTables, elseValue));
        }
        sql.append(SqlConsts.END);
        return sql.toString();
    }

    @Override
    public CaseSearchQueryColumn clone() {
        CaseSearchQueryColumn clone = (CaseSearchQueryColumn) super.clone();
        clone.queryColumn = ObjectUtil.clone(this.queryColumn);
        clone.whens = CollectionUtil.cloneArrayList(this.whens);
        clone.elseValue = ObjectUtil.cloneObject(this.elseValue);
        return clone;
    }


    @Override
    String toConditionSql(List<QueryTable> queryTables, Dialect dialect) {
        return WrapperUtil.withBracket(buildSql(queryTables, dialect));
    }

    void addWhen(When when) {
        if (whens == null) {
            whens = new ArrayList<>();
        }
        whens.add(when);
    }


    @Override
    public Object[] getParamValues() {
        Object[] values = OrmConsts.EMPTY_ARRAY;
        if (elseValue instanceof HasParamsColumn) {
            values = ArrayUtil.concat(values, ((HasParamsColumn) elseValue).getParamValues());
        }
        return values;
    }


    public static class When implements CloneSupport<When> {
        private Object searchValue;
        @Setter
        private Object thenValue;

        public When(Object searchValue) {
            this.searchValue = searchValue;
        }

        @Override
        public When clone() {
            try {
                When clone = (When) super.clone();
                clone.searchValue = ObjectUtil.cloneObject(this.searchValue);
                clone.thenValue = ObjectUtil.cloneObject(this.thenValue);
                return clone;
            } catch (CloneNotSupportedException e) {
                throw OrmExceptions.wrap(e);
            }
        }

    }


    public static class Builder {
        private final CaseSearchQueryColumn caseQueryColumn = new CaseSearchQueryColumn();
        private When lastWhen;

        public Builder(QueryColumn queryColumn) {
            this.caseQueryColumn.queryColumn = queryColumn;
        }

        public Then when(Object searchValue) {
            lastWhen = new When(searchValue);
            return new Then(this);
        }

        public Builder else0(Object elseValue) {
            caseQueryColumn.elseValue = elseValue;
            return this;
        }

        public CaseSearchQueryColumn end() {
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
