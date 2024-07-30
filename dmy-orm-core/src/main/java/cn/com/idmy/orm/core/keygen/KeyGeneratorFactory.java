package cn.com.idmy.orm.core.keygen;

import cn.com.idmy.orm.core.exception.OrmExceptions;
import cn.com.idmy.orm.core.exception.locale.LocalizedFormats;
import cn.com.idmy.orm.core.keygen.impl.FlexIDKeyGenerator;
import cn.com.idmy.orm.core.keygen.impl.SnowFlakeIDKeyGenerator;
import cn.com.idmy.orm.core.keygen.impl.ULIDKeyGenerator;
import cn.com.idmy.orm.core.keygen.impl.UUIDKeyGenerator;
import cn.com.idmy.orm.core.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class KeyGeneratorFactory {

    private KeyGeneratorFactory() {
    }

    private static final Map<String, IKeyGenerator> KEY_GENERATOR_MAP = new HashMap<>();

    static {
        /** 内置了 uuid 的生成器，因此主键配置的时候可以直接配置为 @Id(keyType = KeyType.GENERATOR, value = "uuid")
         * {@link cn.com.idmy.orm.annotation.Id}
         */
        register(KeyGenerator.UUID, new UUIDKeyGenerator());
        register(KeyGenerator.FLEX_ID, new FlexIDKeyGenerator());
        register(KeyGenerator.SNOWFLAKE_ID, new SnowFlakeIDKeyGenerator());
        register(KeyGenerator.ULID, new ULIDKeyGenerator());
    }


    /**
     * 获取 主键生成器
     *
     * @param name
     * @return 主键生成器
     */
    public static IKeyGenerator getKeyGenerator(String name) {
        if (StringUtil.isBlank(name)) {
            throw OrmExceptions.wrap(LocalizedFormats.KEY_GENERATOR_BLANK);
        }
        return KEY_GENERATOR_MAP.get(name.trim());
    }


    /**
     * 注册一个主键生成器
     *
     * @param key
     * @param keyGenerator
     */
    public static void register(String key, IKeyGenerator keyGenerator) {
        KEY_GENERATOR_MAP.put(key.trim(), keyGenerator);
    }

}
