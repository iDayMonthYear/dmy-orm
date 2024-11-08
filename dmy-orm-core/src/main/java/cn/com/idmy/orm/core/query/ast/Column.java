package cn.com.idmy.orm.core.query.ast;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Column implements SelectItem {
    private final String name;
}
