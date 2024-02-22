
package cn.com.idmy.orm.spring.boot;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDependsOnDatabaseInitializationDetector;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitializationDetector;

import java.util.Collections;
import java.util.Set;

/**
 * 参考：https://github.com/mybatis/spring-boot-starter/blob/master/mybatis-spring-boot-autoconfigure/src/main/java/org/mybatis/spring/boot/autoconfigure/MybatisDependsOnDatabaseInitializationDetector.java
 * {@link DependsOnDatabaseInitializationDetector} for Mybatis-Flex.
 */
class OrmDependsOnDatabaseInitializationDetector
    extends AbstractBeansOfTypeDependsOnDatabaseInitializationDetector {

    @Override
    protected Set<Class<?>> getDependsOnDatabaseInitializationBeanTypes() {
        return Collections.singleton(SqlSessionTemplate.class);
    }

}
