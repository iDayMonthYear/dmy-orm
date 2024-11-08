package cn.com.idmy.orm.core.query.ast;

public class Or {
    Select root;

    public Or(Select root) {
        this.root  = root;
    }

    public And and() {
        And and = new And(root);
        root.add(and);
        return and;
    }

    public Or eq(String left, Object right) {
        Eq eq = new Eq(root, left, right);
        root.add(eq);
        return this;
    }

    public OrderBy orderBy(String col, boolean desc) {
        OrderBy orderBy = new OrderBy(root, col, desc);
        root.add(orderBy);
        return orderBy;
    }

    public GroupBy groupBy(String col, String... cols) {
        GroupBy tmp = new GroupBy(root, col, cols);
        root.add(tmp);
        return tmp;
    }

    @Override
    public String toString() {
        return " or";
    }

    public String sql() {
        return root.toString();
    }
}
