package cn.com.idmy.orm.core.query.ast;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Accessors(fluent = true, chain = false)
@Getter
public class RootNode  {
    private final List<Object> asts = new ArrayList<>();

    public void addNode(Object ast) {
        asts.add(ast);
    }
}
