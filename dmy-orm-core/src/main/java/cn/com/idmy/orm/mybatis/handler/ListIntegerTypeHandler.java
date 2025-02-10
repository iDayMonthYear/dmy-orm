package cn.com.idmy.orm.mybatis.handler;

import com.alibaba.fastjson2.TypeReference;

import java.util.List;

public class ListIntegerTypeHandler extends JsonTypeHandler<List<Integer>> {
    public ListIntegerTypeHandler() {
        super(new TypeReference<>() {
        });
    }
}