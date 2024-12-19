package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Slf4j
@Getter
@Accessors(fluent = true, chain = false)
public class Delete<T> extends Where<T, Delete<T>> {
    protected Delete(Class<T> entityClass) {
        super(entityClass);
    }

    public static <T, ID> Delete<T> of(MybatisDao<T, ID> dao) {
        return new Delete<>(dao.entityClass());
    }

    @Override
    public Pair<String, List<Object>> sql() {
        return new DeleteSqlGenerator(this).generate();
    }
}