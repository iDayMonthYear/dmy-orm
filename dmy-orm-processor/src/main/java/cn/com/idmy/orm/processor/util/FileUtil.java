package cn.com.idmy.orm.processor.util;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 文件工具类。
 *
 * @author 王帅
 * @since 2023-06-22
 */
public class FileUtil {
    private FileUtil() {
    }

    private static final Set<String> flagFileNames = new HashSet<>(Arrays.asList("pom.xml", "build.gradle", "build.gradle.kts"));

    public static boolean existsBuildFile(File dir) {
        return flagFileNames.stream().anyMatch(fileName -> new File(dir, fileName).exists());
    }

    public static boolean isFromTestSource(String path) {
        return path.contains("test-sources") || path.contains("test-annotations");
    }

    public static boolean isAbsolutePath(String path) {
        return path != null && (path.startsWith("/") || path.contains(":"));
    }

    /**
     * 获取项目的根目录，也就是根节点 pom.xml 所在的目录
     */
    public static String getProjectRootPath(String genFilePath) {
        return getProjectRootPath(new File(genFilePath), 20);
    }

    public static String getProjectRootPath(File file, int depth) {
        if (depth <= 0 || file == null) {
            return null;
        } else if (file.isFile()) {
            return getProjectRootPath(file.getParentFile(), depth - 1);
        } else if (existsBuildFile(file) && !existsBuildFile(file.getParentFile())) {
            return file.getAbsolutePath();
        } else {
            return getProjectRootPath(file.getParentFile(), depth - 1);
        }
    }
}
