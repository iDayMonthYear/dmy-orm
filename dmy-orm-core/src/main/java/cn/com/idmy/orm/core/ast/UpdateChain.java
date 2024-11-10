package cn.com.idmy.orm.core.ast;

import cn.com.idmy.orm.core.OrmDao;
import cn.com.idmy.orm.core.ast.Node.Field;
import cn.com.idmy.orm.core.ast.Node.Set;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
@Accessors(fluent = true, chain = false)
public class UpdateChain<T> extends Sud<T, UpdateChain<T>> {

    private UpdateChain(Class<T> table) {
        super(table);
        sud = this;
    }

    public static <T> UpdateChain<T> of(OrmDao<T> dao) {
        return new UpdateChain<>(dao.entityType());
    }

    public UpdateChain<T> set(String field, Object expr) {
        addNode(new Set(new Field(field), expr));
        return sud;
    }

    public UpdateChain<T> set(FieldGetter<T, ?> field, Object expr) {
        addNode(new Set(new Field(field),  expr));
        return sud;
    }

    public UpdateChain<T> set(FieldGetter<T, ?> field, SqlExpr expr) {
        addNode(new Set(new Field(field), expr));
        return sud;
    }


    @Override
    protected String sql() {
        return UpdateSqlGenerator.gen(this);
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
