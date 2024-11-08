package cn.com.idmy.orm.core.query.ast;

public class Where {
    Select root;

    public Where(Select root) {
        this.root = root;
    }

    public And and() {
        And and = new And(root);
        root.add(and);
        return and;
    }

    @Override
    public String toString() {
        return " where";
    }
}
