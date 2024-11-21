package cn.com.idmy.orm.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
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
@EnableConfigurationProperties(OrmProps.class)
public class OrmAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    MybatisSqlProvider mybatisSqlProvider() {
        return new MybatisSqlProvider();
    }

    @Bean
    EnumWatchListener enumWatchListener(ApplicationContext ctx) {
        return new EnumWatchListener(ctx);
    }

    @Bean
    EnumWatchInterceptor enumWatchInterceptor(ApplicationContext ctx) {
        return new EnumWatchInterceptor(ctx);
    }

    @Bean
    @ConditionalOnMissingBean
    SqlSessionFactory sqlSessionFactory(
            DataSource dataSource,
            EnumWatchInterceptor enumWatchInterceptor
    ) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setConfiguration(new MybatisConfiguration());
//        factory.setPlugins(enumWatchInterceptor);
        return factory.getObject();
    }

    @Bean
    @ConditionalOnMissingBean
    CheckDatabaseColumn checkDatabaseColumn(ApplicationContext ctx, SqlSessionFactory factory, OrmProps props) {
        return props.isCheckDatabaseColumn() ? new CheckDatabaseColumn(ctx, factory) : null;
    }
}