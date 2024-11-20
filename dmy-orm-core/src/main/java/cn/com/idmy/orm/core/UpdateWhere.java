package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.mybatis.MybatisDao;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Slf4j
@Getter
@Setter
@Accessors(fluent = true)
public class UpdateWhere<T> extends LambdaWhere<T, UpdateWhere<T>> {
    private UpdateWhere(Class<T> entityClass) {
        super(entityClass);
    }

    public static <T> UpdateWhere<T> of(MybatisDao<T, ?> dao) {
        return new UpdateWhere<>(dao.entityClass());
    }

    public static <T> UpdateWhere<T> of(MybatisDao<T, ?> dao, T entity) {
        var where = of(dao);
        where.entityClass = (Class<T>) entity.getClass();
        return where;
    }

    @Override
    public Pair<String, List<Object>> sql() {
        return null;
    }
}
