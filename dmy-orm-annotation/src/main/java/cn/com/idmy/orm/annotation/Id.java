
package cn.com.idmy.orm.annotation;

import java.lang.annotation.*;

/**
 * 数据库表中的列信息注解。
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Id {

    /**
     * ID 生成策略，默认为 {@link KeyType#NONE}。
     *
     * @return 生成策略
     */
    KeyType keyType() default KeyType.AUTO;

    /**
     * <p>若 keyType 类型是 sequence， value 则代表的是
     * sequence 序列的 sql 内容。
     * 例如：select SEQ_USER_ID.nextval as id from dual
     *
     * <p>若 keyType 是 Generator，value 则代表的是使用的那个 keyGenerator 的名称。
     */
    String value() default "";

    /**
     * <p>sequence 序列执行顺序。
     *
     * <p>是在 entity 数据插入之前执行，还是之后执行，之后执行的一般是数据主动生成的 id。
     *
     * @return 执行之前还是之后
     */
    boolean before() default true;

    String comment() default "";
}
