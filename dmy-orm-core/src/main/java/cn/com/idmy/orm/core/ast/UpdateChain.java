package cn.com.idmy.orm.core.ast;

import cn.com.idmy.orm.core.OrmDao;
import cn.com.idmy.orm.core.ast.Node.Set;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
@Accessors(fluent = true, chain = false)
public class UpdateChain<T> extends LambdaWhere<T, UpdateChain<T>> {

    private UpdateChain(Class<T> table) {
        super(table);
    }

    public static <T> UpdateChain<T> of(OrmDao<T> dao) {
        return new UpdateChain<>(dao.entityType());
    }

    public UpdateChain<T> set(FieldGetter<T, ?> field, Object expr) {
        return addNode(new Set(field,  expr));
    }

    public UpdateChain<T> set(FieldGetter<T, ?> field, SqlOpExpr expr) {
        return addNode(new Set(field, expr));
    }

    @Override
    protected String sql() {
        return UpdateSqlGenerator.gen(this);
    }
}
