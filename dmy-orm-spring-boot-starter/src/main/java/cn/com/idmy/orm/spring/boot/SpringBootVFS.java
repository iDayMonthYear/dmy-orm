
package cn.com.idmy.orm.spring.boot;

import lombok.Setter;
import org.apache.ibatis.io.VFS;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpringBootVFS extends VFS {
    @Setter
    private static Charset urlDecodingCharset;

    static {
        setUrlDecodingCharset(Charset.defaultCharset());
    }

    private final ResourcePatternResolver resourceResolver;

    public SpringBootVFS() {
        resourceResolver = new PathMatchingResourcePatternResolver(getClass().getClassLoader());
    }

    private static String preserveSubpackageName(String baseUrlString, Resource resource, String rootPath) {
        try {
            return rootPath + (rootPath.endsWith("/") ? "" : "/") + Normalizer
                    .normalize(URLDecoder.decode(resource.getURL().toString(), urlDecodingCharset), Normalizer.Form.NFC)
                    .substring(baseUrlString.length());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    protected List<String> list(URL url, String path) throws IOException {
        String urlString = URLDecoder.decode(url.toString(), urlDecodingCharset);
        String baseUrlString = urlString.endsWith("/") ? urlString : urlString.concat("/");
        Resource[] resources = resourceResolver.getResources(baseUrlString + "**/*.class");
        return Stream.of(resources).map(resource -> preserveSubpackageName(baseUrlString, resource, path))
                .collect(Collectors.toList());
    }
}
