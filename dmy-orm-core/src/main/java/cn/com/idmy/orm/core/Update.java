package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.SqlNode.SqlSet;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static cn.com.idmy.orm.core.Tables.getColumnName;


@Slf4j
@Accessors(fluent = true, chain = false)
public class Update<T> extends Where<T, Update<T>> {
    protected Update(Class<T> entityClass) {
        super(entityClass);
    }

    public static <T, ID> Update<T> of(MybatisDao<T, ID> dao) {
        return new Update<>(dao.entityClass());
    }

    public Update<T> set(FieldGetter<T, ?> field, Object val) {
        return addNode(new SqlSet(getColumnName(entityClass, field), val));
    }

    public Update<T> set(FieldGetter<T, ?> field, SqlOpExpr expr) {
        return addNode(new SqlSet(getColumnName(entityClass, field), expr));
    }

    @Override
    public Pair<String, List<Object>> sql() {
        return new UpdateSqlGenerator(this).generate();
    }
}
