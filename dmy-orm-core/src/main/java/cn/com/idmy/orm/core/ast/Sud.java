package cn.com.idmy.orm.core.ast;

import cn.com.idmy.orm.core.ast.Node.Cond;
import cn.com.idmy.orm.core.ast.Node.Field;
import cn.com.idmy.orm.core.ast.Node.Or;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Accessors(fluent = true, chain = false)
public abstract class Sud<T, SUD extends Sud<T, SUD>> {
    protected final List<Node> nodes = new ArrayList<>();
    protected SUD sud;
    protected final Class<T> table;

    protected abstract String sql();

    protected Sud(Class<T> table) {
        this.table = table;
    }

    protected SUD addNode(Node ast) {
        nodes.add(ast);
        return sud;
    }

    public SUD eq(String field, Object expr) {
        return addNode(new Cond(new Field(field), Op.EQ, expr));
    }

    public SUD eq(FieldGetter<T, ?> field, Object value) {
        return addNode(new Cond(new Field(field), Op.EQ, value));
    }

    public SUD eq(FieldGetter<T, ?> field, SqlExpr expr) {
        return addNode(new Cond(new Field(field), Op.EQ, expr));
    }

    public SUD eq(String field, Object expr, boolean if0) {
        if (if0) {
            return addNode(new Cond(new Field(field), Op.EQ, expr));
        } else {
            return sud;
        }
    }

    public SUD eq(FieldGetter<T, ?> field, Object value, boolean if0) {
        if (if0) {
            return addNode(new Cond(new Field(field), Op.EQ, value));
        } else {
            return sud;
        }
    }

    public SUD eq(FieldGetter<T, ?> field, SqlExpr expr, boolean if0) {
        if (if0) {
            return addNode(new Cond(new Field(field), Op.EQ, expr));
        } else {
            return sud;
        }
    }

    public SUD or() {
        return addNode(new Or());
    }
}