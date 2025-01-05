package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.core.MybatisSqlProvider;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.List;

@AutoConfiguration
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
    SqlSessionFactory sqlSessionFactory(OrmProps props, DataSource dataSource, ApplicationContext ctx, EnumWatchInterceptor enumWatchInterceptor, List<TypeHandler<?>> typeHandlers) throws Exception {
        var bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        var cfg = new MybatisConfiguration();
        if (typeHandlers != null) {
            typeHandlers.forEach(cfg::register);
        }
        bean.setConfiguration(cfg);
        //bean.setPlugins(enumWatchInterceptor);
        bean.setMapperLocations(props.resolveMapperLocations());

        var factory = bean.getObject();
        MybatisSqlProvider.sqlSessionFactory(factory);
        if (props.isCheckDbColumn()) {
            new CheckDbColumn(ctx, factory).scan();
        }
        return factory;
    }
}