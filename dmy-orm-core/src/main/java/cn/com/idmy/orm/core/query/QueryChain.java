package cn.com.idmy.orm.core.query;

public class QueryChain<T> {
    protected WhereCondition whereCondition;

    protected QueryChain<T> addCondition(WhereCondition queryCondition, SqlConnector connector) {
        if (queryCondition != null) {
            if (whereCondition == null) {
                whereCondition = queryCondition;
            } else {
                whereCondition.connect(queryCondition, connector);
            }
        }
        return this;
    }

    public QueryChain<T> and(WhereCondition condition) {
        return addCondition(condition, SqlConnector.AND);
    }

    public QueryChain<T> or(WhereCondition condition) {
        return addCondition(condition, SqlConnector.OR);
    }

    public <V> QueryChain<T> eq(String col, Object value) {
        Column column = Column.of(col);

        and().eq_(value));
        return this;
    }
}
