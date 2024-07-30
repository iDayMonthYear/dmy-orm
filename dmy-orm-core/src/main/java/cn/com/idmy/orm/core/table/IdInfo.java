package cn.com.idmy.orm.core.table;

import cn.com.idmy.orm.annotation.Id;
import cn.com.idmy.orm.annotation.KeyType;
import cn.com.idmy.orm.core.OrmGlobalConfig;
import cn.com.idmy.orm.core.util.StringUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IdInfo extends ColumnInfo {

    /**
     * id 生成策略
     */
    private KeyType keyType;

    /**
     * 1、若 keyType 类型是 sequence， value 则代表的是
     * sequence 序列的 sql 内容
     * 例如：select SEQ_USER_ID.nextval as id from dual
     * <p>
     * 2、若 keyType 是 Generator，value 则代表的是使用的那个 keyGenerator 的名称
     */
    private String value;

    /**
     * sequence 序列内容执行顺序
     *
     * @see org.apache.ibatis.executor.keygen.SelectKeyGenerator
     */
    private Boolean before;

    public IdInfo(Id id) {
        this.keyType = id.keyType();
        this.value = id.value();
        this.before = id.before();
        this.comment = id.comment();
        initDefaultKeyType();
    }

    /**
     * 用户未配置 keyType 是，配置默认的 key Type
     */
    private void initDefaultKeyType() {
        if (this.keyType == null || this.keyType == KeyType.NONE) {
            OrmGlobalConfig.KeyConfig defaultKeyConfig = OrmGlobalConfig.getDefaultConfig().getKeyConfig();
            if (defaultKeyConfig != null) {
                if (defaultKeyConfig.getKeyType() != null) {
                    this.keyType = defaultKeyConfig.getKeyType();
                    this.before = defaultKeyConfig.isBefore();
                }
                if (StringUtil.isBlank(this.value) && StringUtil.isNotBlank(defaultKeyConfig.getValue())) {
                    this.value = defaultKeyConfig.getValue();
                }
            }
        }
    }


}
