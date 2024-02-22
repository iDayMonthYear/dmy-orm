package cn.com.idmy.orm.core.keygen;

import cn.com.idmy.orm.core.exception.OrmExceptions;
import cn.com.idmy.orm.core.exception.locale.LocalizedFormats;
import cn.hutool.core.util.StrUtil;

import java.util.HashMap;
import java.util.Map;

public class KeyGeneratorFactory {

    private KeyGeneratorFactory() {
    }

    private static final Map<String, IKeyGenerator> KEY_GENERATOR_MAP = new HashMap<>();

    /**
     * 获取 主键生成器
     *
     * @param name
     * @return 主键生成器
     */
    public static IKeyGenerator getKeyGenerator(String name) {
        if (StrUtil.isBlank(name)) {
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
