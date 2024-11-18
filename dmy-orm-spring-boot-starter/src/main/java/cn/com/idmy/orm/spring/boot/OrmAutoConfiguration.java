package cn.com.idmy.orm.spring.boot;

import cn.com.idmy.orm.core.mybatis.MybatisConfiguration;
import cn.com.idmy.orm.core.mybatis.MybatisSqlProvider;
import cn.com.idmy.orm.core.mybatis.handler.JSONArrayTypeHandler;
import cn.com.idmy.orm.core.mybatis.handler.JSONObjectTypeHandler;
import cn.com.idmy.orm.spring.EnumWatchInterceptor;
import cn.com.idmy.orm.spring.EnumWatchListener;
import cn.com.idmy.orm.spring.OrmProperties;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.EnumTypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ConditionalOnClass({SqlSessionFactory.class})
@EnableConfigurationProperties(OrmProperties.class)
public class OrmAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource, EnumWatchInterceptor enumWatchInterceptor) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);

        // 使用自定义的 MybatisConfiguration
        factory.setConfiguration(new MybatisConfiguration());

        // 配置类型处理器
        factory.setTypeHandlers(
                new EnumTypeHandler<>(Enum.class),
                new JSONObjectTypeHandler(),
                new JSONArrayTypeHandler()
        );

        // 配置插件
        factory.setPlugins(enumWatchInterceptor);

        return factory.getObject();
    }

    @Bean
    @ConditionalOnMissingBean
    public MybatisSqlProvider mybatisSqlProvider() {
        return new MybatisSqlProvider();
    }

    @Bean
    public EnumWatchListener springEnumWatchListener(ApplicationContext applicationContext) {
        return new EnumWatchListener(applicationContext);
    }
}