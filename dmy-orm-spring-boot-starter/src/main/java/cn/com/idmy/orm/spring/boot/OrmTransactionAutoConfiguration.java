
package cn.com.idmy.orm.spring.boot;

import cn.com.idmy.orm.core.row.Db;
import cn.com.idmy.orm.spring.OrmTransactionManager;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

/**
 * MyBatis-Flex 事务自动配置。
 * @author michael
 */
@ConditionalOnClass(Db.class)
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter({OrmAutoConfiguration.class})
@AutoConfigureBefore({TransactionAutoConfiguration.class})
public class OrmTransactionAutoConfiguration implements TransactionManagementConfigurer {

    /**
     * 这里使用 final 修饰属性是因为：<br>
     * <p>
     * 1、调用 {@link #annotationDrivenTransactionManager} 方法会返回 TransactionManager 对象<br>
     * 2、{@code @Bean} 注入又会返回 TransactionManager 对象<br>
     * <p>
     * 需要保证两个对象的一致性。
     */
    private final OrmTransactionManager ormTransactionManager = new OrmTransactionManager();

    @NonNull
    @Override
    @Bean(name = "transactionManager")
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return ormTransactionManager;
    }
}
