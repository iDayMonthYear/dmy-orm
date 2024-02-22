
package cn.com.idmy.orm.spring.boot;

import org.mybatis.spring.SqlSessionFactoryBean;

/**
 * 参考：https://github.com/mybatis/spring-boot-starter/blob/master/mybatis-spring-boot-autoconfigure/src/main/java/org/mybatis/spring/boot/autoconfigure/SqlSessionFactoryBeanCustomizer.java
 *
 * 为 FlexSqlSessionFactoryBean 做自定义的配置支持。
 *
 * @see cn.com.idmy.orm.spring.FlexSqlSessionFactoryBean
 */
@FunctionalInterface
public interface SqlSessionFactoryBeanCustomizer {

    /**
     * 自定义 {@link SqlSessionFactoryBean}。
     *
     * @param factoryBean FlexSqlSessionFactoryBean
     */
    void customize(SqlSessionFactoryBean factoryBean);
}
