package cn.com.idmy.orm.core.ast;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.OrmDao;
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
    private T entity;

    private UpdateWhere(Class<T> table) {
        super(table);
    }

    public static <T> UpdateWhere<T> of(OrmDao<T> dao) {
        return new UpdateWhere<>(dao.entityType());
    }

    public static <T> UpdateWhere<T> of(OrmDao<T> dao, T entity) {
        UpdateWhere<T> where = of(dao);
        where.entity = entity;
        return where;
    }

    @Override
    public Pair<String, List<Object>> sql() {
        return null;
    }
}
