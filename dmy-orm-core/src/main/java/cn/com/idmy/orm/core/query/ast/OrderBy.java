package cn.com.idmy.orm.core.query.ast;

import lombok.AllArgsConstructor;
import org.dromara.hutool.core.text.StrUtil;

import java.util.ArrayList;
import java.util.List;

public class OrderBy {
    Select root;
    List<OrderByItem> items;

    @AllArgsConstructor(staticName = "of")
    private static class OrderByItem {
        String name;
        boolean desc;
    }


    public OrderBy(Select root) {
        this.root = root;
    }

    public OrderBy(Select root, String name, boolean desc) {
        this(root);
        if (items == null) {
            items = new ArrayList<>(2);
        }
        items.add(OrderByItem.of(name, desc));
    }

    @Override
    public String toString() {
        OrderByItem first = items.getFirst();
        return StrUtil.format(" order by {} {}", first.name, first.desc ? "desc" : "");
    }

    public String sql() {
        return root.toString();
    }
}
