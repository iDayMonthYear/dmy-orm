package cn.com.idmy.orm.spring.boot;

import cn.com.idmy.orm.core.datasource.DataSourceBuilder;
import cn.com.idmy.orm.core.datasource.DataSourceDecipher;
import cn.com.idmy.orm.core.datasource.DataSourceManager;
import cn.com.idmy.orm.core.datasource.OrmDataSource;
import cn.com.idmy.orm.spring.boot.OrmProperties.SeataConfig;
import cn.com.idmy.orm.spring.datasource.DataSourceAdvice;
import io.seata.rm.datasource.DataSourceProxy;
import io.seata.rm.datasource.xa.DataSourceProxyXA;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import javax.sql.DataSource;
import java.util.Map;


/**
 * MyBatis-Flex 多数据源的配置支持。
 *
 * @author michael
 */
@ConditionalOnMybatisFlexDatasource()
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OrmProperties.class)
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
@AutoConfigureBefore(value = DataSourceAutoConfiguration.class, name = "com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure")
public class MultiDataSourceAutoConfiguration {
    private final Map<String, Map<String, String>> dataSourceProperties;
    private final SeataConfig seataConfig;
    protected final DataSourceDecipher dataSourceDecipher;

    public MultiDataSourceAutoConfiguration(OrmProperties properties, ObjectProvider<DataSourceDecipher> dataSourceDecipherProvider) {
        dataSourceProperties = properties.getDatasource();
        dataSourceDecipher = dataSourceDecipherProvider.getIfAvailable();
        seataConfig = properties.getSeataConfig();
    }

    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource() {
        OrmDataSource ormDataSource = null;
        if (dataSourceProperties != null && !dataSourceProperties.isEmpty()) {
            if (dataSourceDecipher != null) {
                DataSourceManager.setDataSourceDecipher(dataSourceDecipher);
            }
            for (Map.Entry<String, Map<String, String>> entry : dataSourceProperties.entrySet()) {
                DataSource dataSource = new DataSourceBuilder(entry.getValue()).build();
                DataSourceManager.decryptDataSource(dataSource);
                if (seataConfig != null && seataConfig.isEnable()) {
                    if (seataConfig.getSeataMode() == OrmProperties.SeataMode.XA) {
                        dataSource = new DataSourceProxyXA(dataSource);
                    } else {
                        dataSource = new DataSourceProxy(dataSource);
                    }
                }
                if (ormDataSource == null) {
                    ormDataSource = new OrmDataSource(entry.getKey(), dataSource, false);
                } else {
                    ormDataSource.addDataSource(entry.getKey(), dataSource, false);
                }
            }
        }
        return ormDataSource;
    }

    @Bean
    @ConditionalOnMissingBean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public DataSourceAdvice dataSourceAdvice() {
        return new DataSourceAdvice();
    }

}
