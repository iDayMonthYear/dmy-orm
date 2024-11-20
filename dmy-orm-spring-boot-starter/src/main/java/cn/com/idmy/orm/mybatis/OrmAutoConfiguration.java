package cn.com.idmy.orm.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.sql.DataSource;

@Configuration
@ConditionalOnClass({SqlSessionFactory.class})
@EnableConfigurationProperties(OrmProperties.class)
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
    InsertInterceptor insertInterceptor() {
        return new InsertInterceptor();
    }

    @Lazy
    @Bean
    @ConditionalOnMissingBean
    SqlSessionFactory sqlSessionFactory(
        DataSource dataSource, 
        EnumWatchInterceptor enumWatchInterceptor,
        InsertInterceptor insertInterceptor
    ) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setConfiguration(new MybatisConfiguration());
        factory.setPlugins(enumWatchInterceptor, insertInterceptor);
        return factory.getObject();
    }

    @Bean
    public static KeyPropertyAnnotationProcessor keyPropertyAnnotationProcessor() {
        return new KeyPropertyAnnotationProcessor();
    }
}