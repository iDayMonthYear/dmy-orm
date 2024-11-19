package cn.com.idmy.orm.ast;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.ast.Node.Set;
import cn.com.idmy.orm.mybatis.MybatisDao;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Slf4j
@Getter
@Accessors(fluent = true, chain = false)
public class UpdateChain<T> extends LambdaWhere<T, UpdateChain<T>> {

    private UpdateChain(Class<T> table) {
        super(table);
    }

    public static <T> UpdateChain<T> of(MybatisDao<T, ?> dao) {
        return new UpdateChain<>(dao.entityType());
    }

    public UpdateChain<T> set(FieldGetter<T, ?> field, Object expr) {
        return addNode(new Set(field,  expr));
    }

    public UpdateChain<T> set(FieldGetter<T, ?> field, SqlOpExpr expr) {
        return addNode(new Set(field, expr));
    }

    @Override
    public Pair<String, List<Object>> sql() {
        return UpdateSqlGenerator.gen(this);
    }
}
