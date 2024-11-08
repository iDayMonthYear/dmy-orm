package cn.com.idmy.orm.core.query.ast;

import java.util.ArrayList;
import java.util.List;


public class Select {
    private List<SelectItem> items = new ArrayList<>();

    public Select(SelectItem... items) {
        this.items = List.of(items);
    }

    public From from() {
        return new From();
    }

}
