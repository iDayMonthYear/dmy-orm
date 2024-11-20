package cn.com.idmy.orm.mybatis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "dmy.orm")
public class OrmProps {
}