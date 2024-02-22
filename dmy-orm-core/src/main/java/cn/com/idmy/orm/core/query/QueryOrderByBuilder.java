package cn.com.idmy.orm.core.query;


import cn.com.idmy.orm.core.util.LambdaGetter;
import cn.com.idmy.orm.core.util.LambdaUtil;

/**
 * 排序字段构建器
 *
 * @author michael
 */
@SuppressWarnings("unchecked")
public class QueryOrderByBuilder<Wrapper extends QueryWrapper> {
    private final Wrapper queryWrapper;
    private final QueryColumn orderByColumn;

    public <T> QueryOrderByBuilder(Wrapper queryWrapper, LambdaGetter<T> getter) {
        this.queryWrapper = queryWrapper;
        this.orderByColumn = LambdaUtil.getQueryColumn(getter);
    }

    public Wrapper asc() {
        return (Wrapper) queryWrapper.orderBy(orderByColumn.asc());
    }

    public Wrapper desc() {
        return (Wrapper) queryWrapper.orderBy(orderByColumn.desc());
    }
}
