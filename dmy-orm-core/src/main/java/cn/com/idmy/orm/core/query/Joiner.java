package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.util.LambdaGetter;
import cn.com.idmy.orm.core.util.LambdaUtil;

import java.util.ListIterator;
import java.util.function.Consumer;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/1/14
 */
public class Joiner<M extends QueryWrapper> {

    private final M queryWrapper;
    private final Join join;

    public Joiner(M queryWrapper, Join join) {
        this.queryWrapper = queryWrapper;
        this.join = join;
    }

    /**
     * <p>推荐写法：
     * <pre>
     * {@code leftJoin(ACCOUNT.as("a")).on(...);}
     * </pre>
     * <p>或者：
     * <pre>{@code
     * AccountTableDef a = ACCOUNT.as("a");
     * leftJoin(a).on(...);
     * }</pre>
     */
    public Joiner<M> as(String alias) {
        join.queryTable = join.getQueryTable().as(alias);
        ListIterator<QueryTable> itr = queryWrapper.joinTables.listIterator();
        while (itr.hasNext()) {
            if (itr.next().isSameTable(join.queryTable)) {
                itr.set(join.queryTable);
                break;
            }
        }
        return this;
    }

    public M on(String on) {
        join.on(new RawQueryCondition(on));
        return queryWrapper;
    }

    public M on(QueryCondition on) {
        join.on(on);
        return queryWrapper;
    }

    public M on(Consumer<QueryWrapper> consumer) {
        QueryWrapper newWrapper = new QueryWrapper();
        consumer.accept(newWrapper);
        join.on(newWrapper.whereQueryCondition);
        return queryWrapper;
    }

    public <T, K> M on(LambdaGetter<T> column1, LambdaGetter<K> column2) {
        QueryCondition queryCondition = LambdaUtil.getQueryColumn(column1).eq(LambdaUtil.getQueryColumn(column2));
        join.on(queryCondition);
        return queryWrapper;
    }


}

