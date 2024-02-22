
package cn.com.idmy.orm.annotation;

import java.lang.annotation.*;

/**
 * 多数据源切换注解。
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface UseDataSource {

    /**
     * 使用哪个数据源，配置的是 Mybatis 的 environmentId 属性。
     */
    String value();
}
