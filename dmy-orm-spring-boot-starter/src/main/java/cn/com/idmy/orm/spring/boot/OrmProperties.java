package cn.com.idmy.orm.spring.boot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "dmy.orm")
public class OrmProperties {
    // 可以添加其他必要的配置属性
} 