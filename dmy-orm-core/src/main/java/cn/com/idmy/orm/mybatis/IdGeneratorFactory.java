package cn.com.idmy.orm.mybatis;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IdGeneratorFactory {
    private static final Map<String, IdGenerator> idGenerator = new HashMap<>();

    public static IdGenerator getGenerator(@NonNull String key) {
        return idGenerator.get(key.trim());
    }

    public static void register(String key, IdGenerator generator) {
        idGenerator.put(key.trim(), generator);
    }
}
