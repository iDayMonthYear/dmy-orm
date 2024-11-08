package cn.com.idmy.orm.core.query.ast;

public class And {
    Select root;

    public And(Select root) {
        this.root = root;
    }

    public And eq(String col, Object val) {
        Eq eq = new Eq(root, col, val);
        root.add(eq);
        return this;
    }

    public Or or() {
        Or or = new Or(root);
        root.add(or);
        return or;
    }

    public GroupBy groupBy(String col, String... cols) {
        GroupBy tmp = new GroupBy(root, col, cols);
        root.add(tmp);
        return tmp;
    }

    public OrderBy orderBy(String col, boolean desc) {
        OrderBy orderBy = new OrderBy(root, col, desc);
        root.add(orderBy);
        return orderBy;
    }

    @Override
    public String toString() {
        return " and";
    }

    public String sql() {
        return root.toString();
    }
}