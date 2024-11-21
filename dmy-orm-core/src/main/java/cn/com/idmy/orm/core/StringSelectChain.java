package cn.com.idmy.orm.core;

import cn.com.idmy.orm.core.Node.Limit;
import cn.com.idmy.orm.core.Node.Offset;
import cn.com.idmy.orm.core.Node.SelectColumn;
import cn.com.idmy.orm.mybatis.MybatisDao;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Accessors(fluent = true, chain = true)
public class StringSelectChain<T> extends SelectChain<T> implements StringWhere<T, StringSelectChain<T>> {

    @Override
    public StringSelectChain<T> addNode(Node node) {
        super.addNode(node);
        return this;
    }

    protected StringSelectChain(Class<T> entity) {
        super(entity);
    }

    public static <T> StringSelectChain<T> of(MybatisDao<T, ?> dao) {
        return new StringSelectChain<>(dao.entityClass());
    }

    public StringSelectChain<T> select(String... cols) {
        for (String c : cols) {
            addNode(new SelectColumn(c));
        }
        return this;
    }

    public StringSelectChain<T> limit(int limit) {
        addNode(new Limit(limit));
        return this;
    }

    public StringSelectChain<T> offset(int offset) {
        addNode(new Offset(offset));
        return this;
    }
}
