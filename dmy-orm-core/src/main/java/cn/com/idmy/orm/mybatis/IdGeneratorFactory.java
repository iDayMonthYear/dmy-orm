package cn.com.idmy.orm.mybatis;


import cn.com.idmy.base.IdGenerator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IdGeneratorFactory {
    private static final Map<String, IdGenerator<?>> idGenerator = new ConcurrentHashMap<>(1);

    public static IdGenerator<?> getGenerator(@NonNull String key) {
        return idGenerator.get(key.trim());
    }

    public static void register(String key, IdGenerator<?> generator) {
        idGenerator.put(key.trim(), generator);
    }
}
