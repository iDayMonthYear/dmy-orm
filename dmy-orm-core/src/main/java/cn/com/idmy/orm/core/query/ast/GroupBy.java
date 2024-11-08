package cn.com.idmy.orm.core.query.ast;

import org.dromara.hutool.core.text.StrUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupBy {
    Select root;
    List<String> items;

    public GroupBy(Select root) {
        this.root = root;
    }

    public GroupBy(Select root, String name, String... names) {
        this(root);
        if (items == null) {
            items = new ArrayList<>(2);
        }
        items.add(name);
        if (names != null) {
            Collections.addAll(items, names);
        }
    }

    public Having having(String expr) {
        Having tmp = new Having(root, expr);
        root.add(tmp);
        return tmp;
    }

    public OrderBy orderBy(String col, boolean desc) {
        OrderBy tmp = new OrderBy(root, col, desc);
        root.add(tmp);
        return tmp;
    }

    @Override
    public String toString() {
        return StrUtil.format(" group by {} {}");
    }

    public String sql() {
        return root.toString();
    }
}
