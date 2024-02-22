
package cn.com.idmy.orm.annotation;

/**
 * ID 生成策略。
 */
public enum KeyType {

    /**
     * 自增的方式。
     */
    AUTO,

    /**
     * <p>通过执行数据库 sql 生成。
     *
     * <p>例如：select SEQ_USER_ID.nextval as id from dual
     */
    SEQUENCE,

    /**
     * 通过 IKeyGenerator 生成器生成。
     */
    GENERATOR,

    /**
     * 其他方式，比如在代码层用户手动设置。
     */
    NONE,
}
