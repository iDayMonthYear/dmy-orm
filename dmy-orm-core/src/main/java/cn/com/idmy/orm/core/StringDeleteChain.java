package cn.com.idmy.orm.core;

import cn.com.idmy.orm.mybatis.MybatisDao;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Accessors(fluent = true, chain = false)
public class StringDeleteChain<T> extends DeleteChain<T> implements StringWhere<T, StringDeleteChain<T>> {

    @Override
    public StringDeleteChain<T> addNode(Node node) {
        super.addNode(node);
        return this;
    }

    protected StringDeleteChain(Class<T> entity) {
        super(entity);
    }

    public static <T> StringDeleteChain<T> of(MybatisDao<T, ?> dao) {
        return new StringDeleteChain<>(dao.entityClass());
    }
}
