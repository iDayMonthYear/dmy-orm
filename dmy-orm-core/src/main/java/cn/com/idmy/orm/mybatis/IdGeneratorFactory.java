package cn.com.idmy.orm.mybatis;


import cn.com.idmy.base.IdGenerator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IdGeneratorFactory {
    private static final Map<String, IdGenerator<Object>> idGenerator = new HashMap<>(1);

    public static IdGenerator<Object> getGenerator(@NonNull String key) {
        return idGenerator.get(key.trim());
    }

    public static void register(String key, IdGenerator<Object> generator) {
        idGenerator.put(key.trim(), generator);
    }
}
