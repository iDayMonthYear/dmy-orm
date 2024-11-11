package cn.com.idmy.orm.core.ast;

import cn.com.idmy.orm.core.ast.Node.Cond;
import cn.com.idmy.orm.core.ast.Node.Field;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true, chain = false)
public abstract class StringWhere<T, WHERE extends StringWhere<T, WHERE>> extends LambdaWhere<T, WHERE> {
    protected StringWhere(Class<T> table) {
        super(table);
    }

    public WHERE eq(String field, Object expr) {
        return addNode(new Cond(new Field(field), Op.EQ, expr));
    }

    public WHERE eq(String field, Object expr, boolean if0) {
        if (if0) {
            return addNode(new Cond(new Field(field), Op.EQ, expr));
        } else {
            return typedThis;
        }
    }
}