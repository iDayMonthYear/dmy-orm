package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.OrmConfig;
import cn.com.idmy.orm.core.SqlProvider;
import org.apache.ibatis.session.SqlSessionFactory;
import org.dromara.hutool.core.collection.CollUtil;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;
import java.util.Objects;

@Order
@AutoConfiguration
@ConditionalOnClass({SqlSessionFactory.class})
@EnableConfigurationProperties(OrmProps.class)
public class OrmAutoConfiguration {
    @Bean
    @Lazy
    @ConditionalOnMissingBean
    SqlProvider mybatisSqlProvider() {
        return new SqlProvider();
    }

    @Bean
    @Lazy
    EnumWatchListener enumWatchListener(ApplicationContext ctx) {
        return new EnumWatchListener(ctx);
    }

    @Bean
    @Lazy
    EnumWatchInterceptor enumWatchInterceptor(ApplicationContext ctx) {
        return new EnumWatchInterceptor(ctx);
    }

    @Bean
    @Lazy
    protected SqlSessionFactory sqlSessionFactory(OrmProps props, DataSource dataSource, ApplicationContext ctx) throws Exception {
        setConfig(props);
        var bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        var configuration = new MybatisConfiguration();
        bean.setConfiguration(configuration);
        bean.setMapperLocations(props.resolveMapperLocations());

        var factory = bean.getObject();
        SqlProvider.sqlSessionFactory(Objects.requireNonNull(factory));
        if (Boolean.TRUE.equals(props.getCheckDbColumn())) {
            new CheckDbColumn(ctx, factory).scan();
        }
        return factory;
    }

    protected static void setConfig(OrmProps props) {
        var cfg = OrmConfig.config();
        if (props.getIEnumValueEnabled() != null) cfg.iEnumValueEnabled(props.getIEnumValueEnabled());
        if (props.getTableNameStrategy() != null) cfg.columnNameStrategy(props.getTableNameStrategy());
        if (props.getColumnNameStrategy() != null) cfg.columnNameStrategy(props.getColumnNameStrategy());
        if (CollUtil.isNotEmpty(props.getCrudInterceptors()))
            props.getCrudInterceptors().forEach(OrmConfig::registerInterceptor);
    }
}