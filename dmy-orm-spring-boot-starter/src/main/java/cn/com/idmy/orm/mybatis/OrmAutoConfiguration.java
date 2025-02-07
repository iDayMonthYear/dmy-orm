package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.OrmConfig;
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
import org.springframework.context.annotation.Lazy;

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
    @Lazy
    SqlSessionFactory sqlSessionFactory(OrmProps props, DataSource dataSource, ApplicationContext ctx, EnumWatchInterceptor enumWatchInterceptor) throws Exception {
        OrmConfig cfg = OrmConfig.config();
        cfg.enableIEnumValue(props.isEnableIEnumValue());
        var bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        var configuration = new MybatisConfiguration();
        if (ctx.containsBean("mybatisTypeHandlers")) {
            var obj = ctx.getBean("mybatisTypeHandlers");
            if (obj instanceof List<?> typeHandlers) {
                for (Object o : typeHandlers) {
                    configuration.register((TypeHandler<?>) o);
                }
            }
        }
        bean.setConfiguration(configuration);
        bean.setPlugins(enumWatchInterceptor);
        bean.setMapperLocations(props.resolveMapperLocations());

        var factory = bean.getObject();
        MybatisSqlProvider.sqlSessionFactory(factory);
        if (props.isCheckDbColumn()) {
            new CheckDbColumn(ctx, factory).scan();
        }
        return factory;
    }
}