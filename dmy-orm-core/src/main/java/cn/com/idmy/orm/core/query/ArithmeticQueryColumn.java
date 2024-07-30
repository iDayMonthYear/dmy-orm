package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.constant.SqlConsts;
import cn.com.idmy.orm.core.dialect.Dialect;
import cn.com.idmy.orm.core.exception.OrmExceptions;
import cn.com.idmy.orm.core.util.CollectionUtil;
import cn.com.idmy.orm.core.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cn.com.idmy.orm.core.constant.SqlConsts.DIVISION_SIGN;
import static cn.com.idmy.orm.core.constant.SqlConsts.MINUS_SIGN;
import static cn.com.idmy.orm.core.constant.SqlConsts.MULTIPLICATION_SIGN;
import static cn.com.idmy.orm.core.constant.SqlConsts.PLUS_SIGN;

public class ArithmeticQueryColumn extends QueryColumn implements HasParamsColumn {

    private List<ArithmeticInfo> arithmeticInfos;

    public ArithmeticQueryColumn(Object value) {
        arithmeticInfos = new ArrayList<>();
        arithmeticInfos.add(new ArithmeticInfo(value));
    }

    @Override
    public QueryColumn add(QueryColumn queryColumn) {
        arithmeticInfos.add(new ArithmeticInfo(PLUS_SIGN, queryColumn));
        return this;
    }

    @Override
    public QueryColumn add(Number number) {
        arithmeticInfos.add(new ArithmeticInfo(PLUS_SIGN, number));
        return this;
    }

    @Override
    public QueryColumn subtract(QueryColumn queryColumn) {
        arithmeticInfos.add(new ArithmeticInfo(MINUS_SIGN, queryColumn));
        return this;
    }

    @Override
    public QueryColumn subtract(Number number) {
        arithmeticInfos.add(new ArithmeticInfo(MINUS_SIGN, number));
        return this;
    }

    @Override
    public QueryColumn multiply(QueryColumn queryColumn) {
        arithmeticInfos.add(new ArithmeticInfo(MULTIPLICATION_SIGN, queryColumn));
        return this;
    }

    @Override
    public QueryColumn multiply(Number number) {
        arithmeticInfos.add(new ArithmeticInfo(MULTIPLICATION_SIGN, number));
        return this;
    }

    @Override
    public QueryColumn divide(QueryColumn queryColumn) {
        arithmeticInfos.add(new ArithmeticInfo(DIVISION_SIGN, queryColumn));
        return this;
    }

    @Override
    public QueryColumn divide(Number number) {
        arithmeticInfos.add(new ArithmeticInfo(DIVISION_SIGN, number));
        return this;
    }


    @Override
    String toSelectSql(List<QueryTable> queryTables, Dialect dialect) {
        StringBuilder sql = new StringBuilder();
        for (int i = 0; i < arithmeticInfos.size(); i++) {
            sql.append(arithmeticInfos.get(i).toSql(queryTables, dialect, i));
        }
        if (StringUtil.isNotBlank(alias)) {
            return WrapperUtil.withAlias(sql.toString(), alias, dialect);
        }
        return sql.toString();
    }

    @Override
    public ArithmeticQueryColumn clone() {
        ArithmeticQueryColumn clone = (ArithmeticQueryColumn) super.clone();
        // deep clone ...
        clone.arithmeticInfos = CollectionUtil.cloneArrayList(this.arithmeticInfos);
        return clone;
    }


    @Override
    String toConditionSql(List<QueryTable> queryTables, Dialect dialect) {
        StringBuilder sql = new StringBuilder();
        for (int i = 0; i < arithmeticInfos.size(); i++) {
            sql.append(arithmeticInfos.get(i).toSql(queryTables, dialect, i));
        }
        return SqlConsts.BRACKET_LEFT + sql + SqlConsts.BRACKET_RIGHT;
    }

    @Override
    public Object[] getParamValues() {
        return arithmeticInfos.stream()
                .map(arithmeticInfo -> arithmeticInfo.value)
                .filter(HasParamsColumn.class::isInstance)
                .map(value -> ((HasParamsColumn) value).getParamValues())
                .flatMap(Arrays::stream)
                .toArray();
    }


    static class ArithmeticInfo implements CloneSupport<ArithmeticInfo> {

        private final String symbol;
        private final Object value;

        public ArithmeticInfo(Object value) {
            this(null, value);
        }

        public ArithmeticInfo(String symbol, Object value) {
            this.symbol = symbol;
            this.value = value;
        }

        private String toSql(List<QueryTable> queryTables, Dialect dialect, int index) {
            String valueSql;
            if (value instanceof QueryColumn) {
                valueSql = ((QueryColumn) value).toConditionSql(queryTables, dialect);
            } else {
                valueSql = String.valueOf(value);
            }
            return index == 0 ? valueSql : symbol + valueSql;
        }

        @Override
        public ArithmeticInfo clone() {
            try {
                return (ArithmeticInfo) super.clone();
            } catch (CloneNotSupportedException e) {
                throw OrmExceptions.wrap(e);
            }
        }

    }

}
