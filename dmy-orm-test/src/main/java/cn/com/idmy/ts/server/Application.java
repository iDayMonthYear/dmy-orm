package cn.com.idmy.ts.server;

import cn.com.idmy.orm.OrmConfig;
import cn.com.idmy.ts.server.config.AuditInterceptor;
import cn.com.idmy.ts.server.config.QueryInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@EnableConfigurationProperties
@ConfigurationPropertiesScan
@ComponentScan("cn.com.idmy.ts")
@MapperScan("cn.com.idmy.ts.server.dao")
public class Application {
    public static void main(String[] args) {
        OrmConfig.registerCrudInterceptor(new QueryInterceptor());
        OrmConfig.registerCrudInterceptor(new AuditInterceptor());
        SpringApplication.run(Application.class, args);
    }
}
