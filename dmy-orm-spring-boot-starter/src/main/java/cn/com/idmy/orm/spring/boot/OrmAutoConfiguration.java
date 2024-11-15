package cn.com.idmy.orm.spring.boot;

import cn.com.idmy.orm.core.provider.MybatisSqlProvider;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.EnumTypeHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({SqlSessionFactory.class})
@EnableConfigurationProperties(OrmProperties.class)
public class OrmAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MybatisSqlProvider mybatisSqlProvider() {
        return new MybatisSqlProvider();
    }

    @Bean
    @ConditionalOnMissingBean
    public EnumTypeHandler<?> enumTypeHandler() {
        return new EnumTypeHandler<>(Enum.class);
    }
} 