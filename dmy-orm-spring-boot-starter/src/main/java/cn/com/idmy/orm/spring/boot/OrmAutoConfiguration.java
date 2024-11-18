package cn.com.idmy.orm.spring.boot;

import cn.com.idmy.orm.core.mybatis.MybatisSqlProvider;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.EnumTypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ConditionalOnClass({SqlSessionFactory.class})
@EnableConfigurationProperties(OrmProperties.class)
public class OrmAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);

        // 配置类型处理器
        factory.setTypeHandlers(new EnumTypeHandler[]{
                new EnumTypeHandler<>(Enum.class)
        });

        return factory.getObject();
    }

    @Bean
    @ConditionalOnMissingBean
    public MybatisSqlProvider mybatisSqlProvider() {
        return new MybatisSqlProvider();
    }
}