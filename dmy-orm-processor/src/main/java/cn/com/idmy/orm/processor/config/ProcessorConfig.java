package cn.com.idmy.orm.processor.config;

import cn.com.idmy.orm.processor.util.FileUtil;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Mybatis Flex 生成配置。
 *
 * @author 王帅
 * @since 2023-06-22
 */
public class ProcessorConfig {

    /**
     * 配置文件名。
     */
    private static final String APT_FILE_NAME = "processor.config";

    /**
     * mybatis-flex.properties
     */
    protected final Properties properties = new Properties();

    public ProcessorConfig(Filer filer) {
        try {
            //target/classes/
            FileObject resource = filer.createResource(StandardLocation.CLASS_OUTPUT, "", "mybatis-flex");
            File classPathFile = new File(resource.toUri()).getParentFile();

            String projectRootPath = FileUtil.getProjectRootPath(classPathFile, 10);

            List<File> aptConfigFiles = new ArrayList<>();

            while (projectRootPath != null && classPathFile != null
                && projectRootPath.length() <= classPathFile.getAbsolutePath().length()) {
                File aptConfig = new File(classPathFile, APT_FILE_NAME);
                if (aptConfig.exists()) {
                    aptConfigFiles.add(aptConfig);
                }
                classPathFile = classPathFile.getParentFile();
            }


            for (File aptConfigFile : aptConfigFiles) {
                try (InputStream stream = Files.newInputStream(aptConfigFile.toPath());
                     Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {

                    Properties config = new Properties();
                    config.load(reader);

                    boolean stopBubbling = false;
                    for (Object key : config.keySet()) {
                        if (!properties.containsKey(key)) {
                            properties.put(key, config.getProperty((String) key));
                        }
                        if ("processor.stopBubbling".equalsIgnoreCase((String) key)
                            && "true".equalsIgnoreCase(String.valueOf(config.getProperty((String) key)))) {
                            stopBubbling = true;
                        }
                    }
                    if (stopBubbling) {
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String get(ConfigurationKey key) {
        return properties.getProperty(key.getConfigKey(), key.getDefaultValue());
    }

}
