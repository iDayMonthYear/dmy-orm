
package cn.com.idmy.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库表信息注解。
 *
 * @author Michael Yang
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Table {
    /**
     * 显式指定表名称。
     */
    String value() default "";

    /**
     * 数据库的 schema（模式）。
     */
    String schema() default "";

    /**
     * 默认使用哪个数据源，若系统找不到该指定的数据源时，默认使用第一个数据源。
     */
    String dataSource() default "";

    /**
     * 默认为 驼峰属性 转换为 下划线字段。
     */
    boolean underline() default false;

    /**
     * 在某些场景下，我们需要手动编写 Mapper，可以通过这个注解来关闭 APT 的 Mapper 生成。
     */
    boolean dao() default true;

    String comment() default "";
}
