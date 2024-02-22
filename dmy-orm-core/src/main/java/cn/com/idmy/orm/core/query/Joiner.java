package cn.com.idmy.orm.core.query;

import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/1/14
 */
@RequiredArgsConstructor
public class Joiner<M> {
    private final M queryWrapper;
    private final Join join;

    public Joiner<M> as(String alias) {
        join.getQueryTable().as(alias);
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
}

