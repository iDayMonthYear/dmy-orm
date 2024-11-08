package cn.com.idmy.orm.core.query.ast;

import org.dromara.hutool.core.text.StrUtil;

public class Having {
    Select root;
    String expr;

    public Having(Select root) {
        this.root = root;
    }

    public Having(Select root, String expr) {
        this(root);
        this.expr = expr;
    }

    public OrderBy orderBy(String col, boolean desc) {
        OrderBy tmp = new OrderBy(root, col, desc);
        root.add(tmp);
        return tmp;
    }

    @Override
    public String toString() {
        return StrUtil.format(" having {}", expr);
    }

    public String sql() {
        return root.toString();
    }
}
