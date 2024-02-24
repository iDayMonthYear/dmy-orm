
package cn.com.idmy.orm.spring.boot;

import org.mybatis.spring.SqlSessionFactoryBean;

@FunctionalInterface
public interface SqlSessionFactoryBeanCustomizer {
    void customize(SqlSessionFactoryBean factoryBean);
}
