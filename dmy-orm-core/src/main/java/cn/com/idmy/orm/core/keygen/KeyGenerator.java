package cn.com.idmy.orm.core.keygen;

public interface KeyGenerator {

    /**
     * uuid 主键生成器
     * {@link cn.com.idmy.orm.core.keygen.impl.UUIDKeyGenerator}
     */
    String UUID = "uuid";

    /**
     * flexId 主键生成器
     * {@link cn.com.idmy.orm.core.keygen.impl.FlexIDKeyGenerator}
     */
    String FLEX_ID = "flexId";

    /**
     * 雪花算法主键生成器
     * {@link cn.com.idmy.orm.core.keygen.impl.SnowFlakeIDKeyGenerator}
     */
    String SNOWFLAKE_ID = "snowFlakeId";

    /**
     * ulid 主键生成器
     * {@link cn.com.idmy.orm.core.keygen.impl.ULIDKeyGenerator}
     */
    String ULID = "ulid";
}
