package cn.com.idmy.orm.mybatis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

@Data
@ConfigurationProperties(prefix = "orm")
public class OrmProps {
    private static final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
    private boolean checkDbColumn = false;
    private boolean enableIEnumValue = false;
    private String[] mapperLocations = new String[]{"classpath*:/dao/**/*.xml"};

    private Resource[] resources(String location) {
        try {
            return resourceResolver.getResources(location);
        } catch (IOException e) {
            return new Resource[0];
        }
    }

    public Resource[] resolveMapperLocations() {
        return Stream.of(Optional.ofNullable(this.mapperLocations).orElse(new String[0]))
                .flatMap(location -> Stream.of(resources(location)))
                .toArray(Resource[]::new);
    }
}