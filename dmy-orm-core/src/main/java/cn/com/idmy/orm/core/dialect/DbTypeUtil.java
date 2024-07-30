package cn.com.idmy.orm.core.dialect;


import cn.com.idmy.orm.core.exception.OrmExceptions;
import cn.com.idmy.orm.core.exception.locale.LocalizedFormats;
import cn.com.idmy.orm.core.util.StringUtil;
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

        if (StringUtil.isNotBlank(jdbcUrl)) {
            return parseDbType(jdbcUrl);
        }

        throw new IllegalStateException("Can not get dataSource jdbcUrl: " + dataSource.getClass().getName());
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
            } catch (Exception e) {
                //ignore
            }
        }

        try (Connection connection = dataSource.getConnection()) {
            return connection.getMetaData().getURL();
        } catch (Exception e) {
            throw OrmExceptions.wrap(e, LocalizedFormats.DATASOURCE_JDBC_URL);
        }
    }

    public static DbType parseDbType(String jdbcUrl) {
        jdbcUrl = jdbcUrl.toLowerCase();
        if (jdbcUrl.contains(":mysql:") || jdbcUrl.contains(":cobar:")) {
            return DbType.MYSQL;
        } else if (jdbcUrl.contains(":h2:")) {
            return DbType.H2;
        } else if (jdbcUrl.contains(":lealone:")) {
            return DbType.LEALONE;
        } else {
            return DbType.OTHER;
        }
    }

    /**
     * 正则匹配，验证成功返回 true，验证失败返回 false
     */
    public static boolean isMatchedRegex(String regex, String jdbcUrl) {
        if (null == jdbcUrl) {
            return false;
        }
        return Pattern.compile(regex).matcher(jdbcUrl).find();
    }

}
