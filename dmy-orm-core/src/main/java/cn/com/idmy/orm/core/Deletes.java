package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Getter
@Accessors(fluent = true, chain = false)
@Slf4j
public class Deletes<T> extends Where<T, Deletes<T>> {
    protected Deletes(Class<T> entityClass) {
        super(entityClass);
    }

    public static <T, ID> Deletes<T> of(MybatisDao<T, ID> dao) {
        return new Deletes<>(dao.entityClass());
    }

    @Override
    public Pair<String, List<Object>> sql() {
        return new DeleteSqlGenerator(this).gen();
    }
}