package cn.com.idmy.orm.core.datasource;

import cn.com.idmy.orm.core.exception.OrmExceptions;
import cn.com.idmy.orm.core.util.ClassUtil;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.lang.reflect.Method;

/**
 * @author michael
 */
@Slf4j
public class DataSourceManager {
    @Getter
    @Setter
    private static DataSourceDecipher dataSourceDecipher;

    @Getter
    @Setter
    private static DataSourceShardingStrategy dataSourceShardingStrategy;

    public static void decryptDataSource(DataSource dataSource) {
        if (dataSourceDecipher == null) {
            return;
        }

        try {
            restartDataSource(dataSource);
        } catch (Exception ignored) {
        }

        for (DataSourceProperty property : DataSourceProperty.values()) {
            Method getterMethod = ClassUtil.getAnyMethod(dataSource.getClass(), property.getGetterMethods());
            if (getterMethod != null) {
                String value = invokeMethod(getterMethod, dataSource);
                if (value != null) {
                    value = dataSourceDecipher.decrypt(property, value);
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
            log.error("Can not invoke method: " + method.getName(), e);
            return null;
        }
    }

    static String getByShardingStrategy(String dataSource, Object mapper, Method method, Object[] args) {
        return dataSourceShardingStrategy != null ? dataSourceShardingStrategy.doSharding(dataSource, mapper, method, args) : null;
    }
}
