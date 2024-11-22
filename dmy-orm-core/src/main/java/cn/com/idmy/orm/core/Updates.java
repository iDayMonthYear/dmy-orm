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
public class Updates<T> extends LambdaWhere<T, Updates<T>> {

    protected Updates(Class<T> entityClass) {
        super(entityClass);
    }

    public static <T> Updates<T> of(MybatisDao<T, ?> dao) {
        return new Updates<>(dao.entityClass());
    }

    public Updates<T> set(ColumnGetter<T, ?> col, Object expr) {
        return addNode(new Set(col,  expr));
    }

    public Updates<T> set(ColumnGetter<T, ?> col, SqlOpExpr expr) {
        return addNode(new Set(col, expr));
    }

    @Override
    public Pair<String, List<Object>> sql() {
        return UpdateSqlGenerator.gen(this);
    }
}
