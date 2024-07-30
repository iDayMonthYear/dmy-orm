package cn.com.idmy.orm.core.keygen.impl;

import cn.com.idmy.orm.core.keygen.IKeyGenerator;

import java.util.UUID;

public class UUIDKeyGenerator implements IKeyGenerator {

    @Override
    public Object generate(Object entity, String keyColumn) {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
