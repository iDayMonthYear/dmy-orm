package cn.com.idmy.orm.core.row;

import cn.com.idmy.orm.annotation.KeyType;
import cn.com.idmy.orm.core.keygen.KeyGenerator;
import cn.com.idmy.orm.core.util.SqlUtil;
import lombok.Getter;

import java.io.Serializable;

/**
 * row 的主键策略
 */
@Getter
public class RowKey implements Serializable {

    /**
     * 自增 ID
     */
    public static final RowKey AUTO = RowKey.of("id", KeyType.AUTO, null, false);

    /**
     * UUID 的 ID
     */
    public static final RowKey UUID = RowKey.of("id", KeyType.GENERATOR, KeyGenerator.UUID, true);

    /**
     * flexId
     */
    public static final RowKey FLEX_ID = RowKey.of("id", KeyType.GENERATOR, KeyGenerator.FLEX_ID, true);

    /**
     * snowFlakeId
     */
    public static final RowKey SNOW_FLAKE_ID = RowKey.of("id", KeyType.GENERATOR, KeyGenerator.SNOWFLAKE_ID, true);

    /**
     * ulid
     */
    public static final RowKey ULID = RowKey.of("id", KeyType.GENERATOR, KeyGenerator.ULID, true);

    public static RowKey of(String keyColumn) {
        SqlUtil.keepColumnSafely(keyColumn);
        RowKey rowKey = new RowKey();
        rowKey.keyColumn = keyColumn;
        return rowKey;
    }

    public static RowKey of(String keyColumn, KeyType keyType) {
        RowKey rowKey = of(keyColumn);
        rowKey.keyType = keyType;
        return rowKey;
    }

    public static RowKey of(String keyColumn, KeyType keyType, String keyTypeValue) {
        RowKey rowKey = of(keyColumn, keyType);
        rowKey.value = keyTypeValue;
        return rowKey;
    }

    public static RowKey of(String keyColumn, KeyType keyType, String keyTypeValue, boolean before) {
        RowKey rowKey = of(keyColumn, keyType, keyTypeValue);
        rowKey.before = before;
        return rowKey;
    }

    /**
     * 主键字段
     */
    protected String keyColumn;

    /**
     * 主键类型
     */
    protected KeyType keyType = KeyType.AUTO;

    /**
     * 主键类型为 Sequence 和 Generator 时的对应的内容
     */
    protected String value;

    /**
     * 是否前执行
     */
    protected boolean before = true;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof RowKey) {
            return keyColumn.equals(((RowKey) o).keyColumn);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return keyColumn.hashCode();
    }

}
