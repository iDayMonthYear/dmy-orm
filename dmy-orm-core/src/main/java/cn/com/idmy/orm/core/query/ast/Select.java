package cn.com.idmy.orm.core.query.ast;

import java.util.ArrayList;
import java.util.List;

public class Select {
    List<Object> asts = new ArrayList<>();
    List<SelectItem> items = new ArrayList<>();

    public Select(SelectItem... items) {
        this.items = List.of(items);
    }

    void add(Object ast) {
        asts.add(ast);
    }

    Object last() {
        return asts.getLast();
    }

    public From from(String table) {
        From from = new From(this, table);
        add(from);
        return from;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("select");
        sb.append(items.isEmpty() ? "*" : items);
        for (Object ast : asts) {
            sb.append(ast);
        }
        return sb.toString().replace("[", "").replace("]", "");
    }
}