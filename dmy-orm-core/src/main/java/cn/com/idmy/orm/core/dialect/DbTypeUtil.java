package cn.com.idmy.orm.core.dialect;


import cn.com.idmy.orm.core.exception.OrmExceptions;
import cn.com.idmy.orm.core.exception.locale.LocalizedFormats;
import cn.hutool.core.util.StrUtil;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.regex.Pattern;

/**
 * DbType 解析 工具类
 */
public class DbTypeUtil {
    private DbTypeUtil() {
    }

    /**
     * 获取当前配置的 DbType
     */
    public static DbType getDbType(DataSource dataSource) {
        String jdbcUrl = getJdbcUrl(dataSource);
        if (StrUtil.isNotBlank(jdbcUrl)) {
            return parseDbType(jdbcUrl);
        } else {
            throw new IllegalStateException("Can not get dataSource jdbcUrl: " + dataSource.getClass().getName());
        }
    }

    /**
     * 通过数据源中获取 jdbc 的 url 配置
     * 符合 HikariCP, druid, c3p0, DBCP, beecp 数据源框架 以及 MyBatis UnpooledDataSource 的获取规则
     * UnpooledDataSource 参考 @{@link UnpooledDataSource#getUrl()}
     *
     * @return jdbc url 配置
     */
    public static String getJdbcUrl(DataSource dataSource) {
        String[] methodNames = new String[]{"getUrl", "getJdbcUrl"};
        for (String methodName : methodNames) {
            try {
                Method method = dataSource.getClass().getMethod(methodName);
                return (String) method.invoke(dataSource);
            } catch (Exception ignore) {
            }
        }

        try (Connection connection = dataSource.getConnection()) {
            return connection.getMetaData().getURL();
        } catch (Exception e) {
            throw OrmExceptions.wrap(e, LocalizedFormats.DATASOURCE_JDBC_URL);
        }
    }


    /**
     * 参考 druid  和 MyBatis-plus 的 JdbcUtils
     *
     * @param jdbcUrl jdbcURL
     * @return 返回数据库类型
     */
    public static DbType parseDbType(String jdbcUrl) {
        jdbcUrl = jdbcUrl.toLowerCase();
        if (jdbcUrl.contains(":mysql:")) {
            return DbType.MYSQL;
        } else {
            return DbType.OTHER;
        }
    }

    /**
     * 正则匹配，验证成功返回 true，验证失败返回 false
     */
    public static boolean isMatchedRegex(String regex, String jdbcUrl) {
        return null != jdbcUrl && Pattern.compile(regex).matcher(jdbcUrl).find();
    }
}
