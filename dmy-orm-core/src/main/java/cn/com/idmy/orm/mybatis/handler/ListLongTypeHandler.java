package cn.com.idmy.orm.mybatis.handler;

import com.alibaba.fastjson2.TypeReference;

import java.util.List;

public class ListLongTypeHandler extends JsonTypeHandler<List<Long>> {
    public ListLongTypeHandler() {
        super(new TypeReference<>() {
        });
    }
}