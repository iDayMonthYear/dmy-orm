package cn.com.idmy.orm.mybatis;


import cn.com.idmy.base.IdGenerator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IdGeneratorFactory {
    private static final Map<String, IdGenerator<?>> idGenerator = new ConcurrentHashMap<>(1);

    public static @Nullable IdGenerator<?> getGenerator(@NonNull String key) {
        return idGenerator.get(key.trim());
    }

    public static void register(@NonNull String key, @NonNull IdGenerator<?> generator) {
        idGenerator.put(key.trim(), generator);
    }
}
