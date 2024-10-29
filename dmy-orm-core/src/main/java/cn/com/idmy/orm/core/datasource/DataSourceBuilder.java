package cn.com.idmy.orm.core.datasource;

import cn.com.idmy.orm.core.exception.OrmExceptions;
import cn.com.idmy.orm.core.exception.locale.LocalizedFormats;
import cn.com.idmy.orm.core.util.ConvertUtil;
import cn.com.idmy.orm.core.util.StringUtil;
import jakarta.annotation.Nullable;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.reflection.invoker.Invoker;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class DataSourceBuilder {

    private static final Map<String, String> dataSourceAlias = new HashMap<>();

    static {
        dataSourceAlias.put("druid", "com.alibaba.druid.pool.DruidDataSource");
        dataSourceAlias.put("hikari", "com.zaxxer.hikari.HikariDataSource");
        dataSourceAlias.put("hikaricp", "com.zaxxer.hikari.HikariDataSource");
        dataSourceAlias.put("lealone", "com.lealone.client.jdbc.JdbcDataSource");
    }

    private final Map<String, String> dataSourceProperties;

    public DataSourceBuilder(Map<String, String> dataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
    }

    public DataSource build() {
        String dataSourceClassName;
        String type = dataSourceProperties.get("type");
        if (StringUtil.isNotBlank(type)) {
            dataSourceClassName = dataSourceAlias.getOrDefault(type, type);
        } else {
            dataSourceClassName = detectDataSourceClass();
        }


        if (StringUtil.isBlank(dataSourceClassName)) {
            if (StringUtil.isBlank(type)) {
                throw OrmExceptions.wrap(LocalizedFormats.DATASOURCE_TYPE_BLANK);
            } else {
                throw OrmExceptions.wrap(LocalizedFormats.DATASOURCE_TYPE_NOT_FIND, type);
            }
        }

        try {
            Class<?> dataSourceClass = Class.forName(dataSourceClassName);
            Object dataSourceObject = dataSourceClass.newInstance();
            setDataSourceProperties(dataSourceObject);
            return (DataSource) dataSourceObject;
        } catch (Exception e) {
            throw OrmExceptions.wrap(e, LocalizedFormats.DATASOURCE_CAN_NOT_INSTANCE, dataSourceClassName);
        }
    }

    private void setDataSourceProperties(Object dataSourceObject) throws Exception {
        Reflector reflector = new Reflector(dataSourceObject.getClass());
        for (String attr : dataSourceProperties.keySet()) {
            String value = dataSourceProperties.get(attr);
            String camelAttr = attrToCamel(attr);
            if ("url".equals(camelAttr) || "jdbcUrl".equals(camelAttr)) {
                if (reflector.hasSetter("url")) {
                    reflector.getSetInvoker("url").invoke(dataSourceObject, new Object[]{value});
                } else if (reflector.hasSetter("jdbcUrl")) {
                    reflector.getSetInvoker("jdbcUrl").invoke(dataSourceObject, new Object[]{value});
                }
            } else {
                if (reflector.hasSetter(camelAttr)) {
                    Invoker setInvoker = reflector.getSetInvoker(camelAttr);
                    setInvoker.invoke(dataSourceObject, new Object[]{ConvertUtil.convert(value, setInvoker.getType())});
                }
            }
        }
    }


    public static String attrToCamel(String string) {
        int strLen = string.length();
        StringBuilder sb = new StringBuilder(strLen);
        for (int i = 0; i < strLen; i++) {
            char c = string.charAt(i);
            if (c == '-') {
                if (++i < strLen) {
                    sb.append(Character.toUpperCase(string.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    @Nullable
    private String detectDataSourceClass() {
        String[] detectClassNames = new String[]{
                "com.alibaba.druid.pool.DruidDataSource",
                "com.zaxxer.hikari.HikariDataSource",
                "com.lealone.client.jdbc.JdbcDataSource",
        };

        for (String detectClassName : detectClassNames) {
            String result = doDetectDataSourceClass(detectClassName);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    @Nullable
    private String doDetectDataSourceClass(String className) {
        try {
            Class.forName(className);
            return className;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

}
