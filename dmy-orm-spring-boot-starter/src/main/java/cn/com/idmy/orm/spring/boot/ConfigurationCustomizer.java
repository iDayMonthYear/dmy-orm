
package cn.com.idmy.orm.spring.boot;

import cn.com.idmy.orm.core.mybatis.OrmConfiguration;

/**
 * 为 {@link OrmConfiguration} 做自定义的配置支持。
 * @author michael
 */
@FunctionalInterface
public interface ConfigurationCustomizer {

    /**
     * 自定义配置 {@link OrmConfiguration}。
     *
     * @param configuration MyBatis Flex Configuration
     */
    void customize(OrmConfiguration configuration);

}
