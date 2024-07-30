package cn.com.idmy.orm.core.datasource;

import cn.com.idmy.orm.core.exception.OrmExceptions;
import cn.com.idmy.orm.core.util.ClassUtil;
import jakarta.annotation.Nullable;
import lombok.Getter;
import org.apache.ibatis.logging.LogFactory;

import javax.sql.DataSource;
import java.lang.reflect.Method;

/**
 * @author michael
 */
public class DataSourceManager {

    @Getter
    private static DataSourceDecipher decipher;

    public static void setDecipher(DataSourceDecipher decipher) {
        DataSourceManager.decipher = decipher;
    }

    @Getter
    private static DataSourceShardingStrategy dataSourceShardingStrategy;

    public static void setDataSourceShardingStrategy(DataSourceShardingStrategy dataSourceShardingStrategy) {
        DataSourceManager.dataSourceShardingStrategy = dataSourceShardingStrategy;
    }

    public static void decryptDataSource(DataSource dataSource) {
        if (decipher == null) {
            return;
        }

        try {
            restartDataSource(dataSource);
        } catch (Exception ignored) {
            // do nothing here.
        }

        for (DataSourceProperty property : DataSourceProperty.values()) {
            Method getterMethod = ClassUtil.getAnyMethod(dataSource.getClass(), property.getGetterMethods());
            if (getterMethod != null) {
                String value = invokeMethod(getterMethod, dataSource);
                if (value != null) {
                    value = decipher.decrypt(property, value);
                    Method setter = ClassUtil.getAnyMethod(dataSource.getClass(), property.getSetterMethods());
                    if (setter != null && value != null) {
                        invokeMethod(setter, dataSource, value);
                    }
                }
            }
        }
    }

    static void restartDataSource(DataSource dataSource) {
        Method restartMethod = ClassUtil.getFirstMethod(ClassUtil.getUsefulClass(dataSource.getClass())
                , method -> "restart".equals(method.getName()) && method.getParameterCount() == 0);
        if (restartMethod != null) {
            try {
                restartMethod.invoke(dataSource);
            } catch (Exception e) {
                throw OrmExceptions.wrap(e);
            }
        }
    }

    @Nullable
    static String invokeMethod(Method method, Object object, Object... params) {
        try {
            return (String) method.invoke(object, params);
        } catch (Exception e) {
            LogFactory.getLog(DataSourceManager.class).error("Can not invoke method: " + method.getName(), e);
        }
        return null;
    }


    static String getShardingDsKey(String dataSource, Object mapper, Method method, Object[] args) {
        return dataSourceShardingStrategy != null ? dataSourceShardingStrategy.doSharding(dataSource, mapper, method, args) : null;
    }
}
