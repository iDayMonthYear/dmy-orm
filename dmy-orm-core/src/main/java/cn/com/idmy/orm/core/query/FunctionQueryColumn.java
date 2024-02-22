package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.OrmConsts;
import cn.com.idmy.orm.core.constant.SqlConsts;
import cn.com.idmy.orm.core.dialect.Dialect;
import cn.com.idmy.orm.core.util.CollectionUtil;
import cn.com.idmy.orm.core.util.SqlUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 数据库 聚合函数，例如 count(id) ，max(account.age) 等等
 */
@Setter
@Getter
public class FunctionQueryColumn extends QueryColumn implements HasParamsColumn {

    protected String fnName;
    protected List<QueryColumn> columns;

    public FunctionQueryColumn(String fnName) {
        SqlUtil.keepColumnSafely(fnName);
        this.fnName = fnName;
        this.columns = new ArrayList<>();
    }

    public FunctionQueryColumn(String fnName, String... columns) {
        this(fnName);
        for (String column : columns) {
            this.columns.add(new QueryColumn(column));
        }
    }

    public FunctionQueryColumn(String fnName, QueryColumn... columns) {
        this(fnName);
        this.columns.addAll(Arrays.asList(columns));
    }

    @Override
    public Object[] getParamValues() {
        if (CollectionUtil.isEmpty(columns)) {
            return OrmConsts.EMPTY_ARRAY;
        }

        List<Object> params = new ArrayList<>();

        for (QueryColumn queryColumn : columns) {
            if (queryColumn instanceof HasParamsColumn) {
                Object[] paramValues = ((HasParamsColumn) queryColumn).getParamValues();
                params.addAll(Arrays.asList(paramValues));
            }
        }

        return params.toArray();
    }

    @Override
    public String toSelectSql(List<QueryTable> queryTables, Dialect dialect) {
        String sql = getSql(queryTables, dialect);
        if (StrUtil.isBlank(alias)) {
            return fnName + WrapperUtil.withBracket(sql);
        }
        return fnName + WrapperUtil.withAlias(sql, alias, dialect);
    }

    @Override
    String toConditionSql(List<QueryTable> queryTables, Dialect dialect) {
        String sql = getSql(queryTables, dialect);
        return fnName + WrapperUtil.withBracket(sql);
    }

    /**
     * <p>获取函数括号里面的 SQL 内容。
     *
     * <p>如果函数括号里面没有内容，就返回空字符串，这样构建出来就是函数名加括号。
     *
     * <p>例如，NOW() 函数的构建：
     * <pre>{@code
     * FunctionQueryColumn c1 = new FunctionQueryColumn("NOW");
     * FunctionQueryColumn c2 = new FunctionQueryColumn("NOW", new StringQueryColumn(""));
     * }</pre>
     */
    private String getSql(List<QueryTable> queryTables, Dialect dialect) {
        if (CollectionUtil.isEmpty(columns)) {
            return SqlConsts.EMPTY;
        }

        String sql = columns.stream()
                .filter(Objects::nonNull)
                .map(c -> c.toSelectSql(queryTables, dialect))
                .collect(Collectors.joining(SqlConsts.DELIMITER));

        if (StrUtil.isBlank(sql)) {
            return SqlConsts.EMPTY;
        }

        return sql;
    }


    @Override
    public String toString() {
        return "FunctionQueryColumn{" +
                "fnName='" + fnName + '\'' +
                ", columns=" + columns +
                '}';
    }

    @Override
    public FunctionQueryColumn clone() {
        FunctionQueryColumn clone = (FunctionQueryColumn) super.clone();
        // deep clone ...
        clone.columns = CollectionUtil.cloneArrayList(this.columns);
        return clone;
    }


}
