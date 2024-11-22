package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.Node.Set;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Slf4j
@Getter
@Accessors(fluent = true, chain = false)
public class Updates<T> extends Where<T, Updates<T>> {

    protected Updates(Class<T> entityClass) {
        super(entityClass);
    }

    public static <T, ID> Updates<T> of(MybatisDao<T, ID> dao) {
        return new Updates<>(dao.entityClass());
    }

    public Updates<T> set(ColumnGetter<T, ?> col, Object val) {
        return addNode(new Set(col,  val));
    }

    public Updates<T> set(ColumnGetter<T, ?> col, SqlOpExpr expr) {
        return addNode(new Set(col, expr));
    }

    @Override
    public Pair<String, List<Object>> sql() {
        return UpdateSqlGenerator.gen(this);
    }
}
