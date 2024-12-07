package cn.com.idmy.ts.server;

import cn.com.idmy.orm.OrmConfig;
import cn.com.idmy.orm.mybatis.handler.JsonTypeHandler;
import cn.com.idmy.ts.server.config.AuditInterceptor;
import cn.com.idmy.ts.server.config.QueryInterceptor;
import cn.com.idmy.ts.server.model.entity.App;
import com.alibaba.fastjson2.TypeReference;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Map;

@EnableAsync
@SpringBootApplication
@EnableConfigurationProperties
@ConfigurationPropertiesScan
@ComponentScan("cn.com.idmy.ts")
@MapperScan("cn.com.idmy.ts.server.dao")
public class Application {
    public static void main(String[] args) {
        OrmConfig.register(App.class, App::json2, new JsonTypeHandler<>(new TypeReference<Map<Integer, Long>>() {
        }));
        OrmConfig.register(new QueryInterceptor());
        OrmConfig.register(new AuditInterceptor());
        SpringApplication.run(Application.class, args);
    }
}
