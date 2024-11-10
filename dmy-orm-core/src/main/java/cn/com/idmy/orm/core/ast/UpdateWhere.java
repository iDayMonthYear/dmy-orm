package cn.com.idmy.orm.core.ast;

import cn.com.idmy.orm.core.OrmDao;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
@Setter
@Accessors(fluent = true, chain = false)
public class UpdateWhere<T> extends Sud<T, UpdateWhere<T>> {
    private T entity;

    private UpdateWhere(Class<T> table) {
        super(table);
        sud = this;
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
    protected String sql() {
        return null;
    }

    @Override
    public String toString() {
        try {
            return sql();
        } catch (Exception e) {
            log.warn("SQL生成失败：{}", e.getMessage());
            return null;
        }
    }
}
