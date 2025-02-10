package cn.com.idmy.orm.mybatis.handler;

import com.alibaba.fastjson2.TypeReference;

import java.util.List;

public class ListStringTypeHandler extends JsonTypeHandler<List<String>> {
    public ListStringTypeHandler() {
        super(new TypeReference<>() {
        });
    }
}