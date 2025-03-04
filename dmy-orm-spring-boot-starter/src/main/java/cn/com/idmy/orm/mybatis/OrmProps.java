package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.OrmConfig.NameStrategy;
import cn.com.idmy.orm.core.CrudInterceptor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Data
@ConfigurationProperties(prefix = "orm")
public class OrmProps {
    private static final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
    private Boolean checkDbColumn;
    private Boolean iEnumValueEnabled;
    private NameStrategy tableNameStrategy;
    private NameStrategy columnNameStrategy;
    private List<CrudInterceptor> crudInterceptors;
    private String[] mapperLocations = {"classpath*:/dao/**/*.xml"};

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