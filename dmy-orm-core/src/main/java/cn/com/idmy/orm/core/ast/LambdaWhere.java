package cn.com.idmy.orm.core.ast;

import cn.com.idmy.orm.core.ast.Node.Cond;
import cn.com.idmy.orm.core.ast.Node.Field;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Accessors(fluent = true, chain = false)
public abstract class LambdaWhere<T, WHERE extends LambdaWhere<T, WHERE>> extends AbstractWhere<T, WHERE> {

    protected LambdaWhere(Class<T> table) {
        super(table);
    }

    public WHERE eq(FieldGetter<T, ?> field, Object value) {
        return addNode(new Cond(new Field(field), Op.EQ, value));
    }

    public WHERE eq(FieldGetter<T, ?> field, SqlExpr expr) {
        return addNode(new Cond(new Field(field), Op.EQ, expr));
    }

    public WHERE eq(FieldGetter<T, ?> field, Object value, boolean if0) {
        if (if0) {
            return addNode(new Cond(new Field(field), Op.EQ, value));
        } else {
            return typedThis;
        }
    }

    public WHERE eq(FieldGetter<T, ?> field, SqlExpr expr, boolean if0) {
        if (if0) {
            return addNode(new Cond(new Field(field), Op.EQ, expr));
        } else {
            return typedThis;
        }
    }
}