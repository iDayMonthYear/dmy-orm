package cn.com.idmy.orm.core.mybatis;

import cn.com.idmy.orm.annotation.UseDataSource;
import cn.com.idmy.orm.core.OrmConfig;
import cn.com.idmy.orm.core.datasource.DataSourceKey;
import cn.com.idmy.orm.core.datasource.OrmDataSource;
import cn.com.idmy.orm.core.dialect.DbType;
import cn.com.idmy.orm.core.dialect.DialectFactory;
import cn.com.idmy.orm.core.row.RowMapper;
import cn.com.idmy.orm.core.table.TableInfo;
import cn.com.idmy.orm.core.table.TableInfoFactory;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Nullable;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author michael
 * @author norkts
 */
public class MapperInvocationHandler implements InvocationHandler {
    private final Object mapper;
    private final OrmDataSource dataSource;

    public MapperInvocationHandler(Object mapper, DataSource dataSource) {
        this.mapper = mapper;
        if (dataSource instanceof OrmDataSource) {
            this.dataSource = (OrmDataSource) dataSource;
        } else {
            this.dataSource = null;
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        boolean needClearDsKey = false;
        boolean needClearDbType = false;
        try {
            //获取用户动态指定，由用户指定数据源，则应该有用户清除
            String dataSourceKey = DataSourceKey.get();
            if (StrUtil.isBlank(dataSourceKey)) {
                //通过 @UseDataSource 或者 @Table(dataSource) 去获取
                String configDataSourceKey = getConfigDataSourceKey(method, proxy);
                if (StrUtil.isNotBlank(configDataSourceKey)) {
                    dataSourceKey = configDataSourceKey;
                    DataSourceKey.use(dataSourceKey);
                    needClearDsKey = true;
                }
            }

            //最终通过数据源 自定义分片 策略去获取
            String shardingDataSourceKey = DataSourceKey.getByShardingStrategy(dataSourceKey, proxy, method, args);
            if (shardingDataSourceKey != null && !shardingDataSourceKey.equals(dataSourceKey)) {
                DataSourceKey.use(shardingDataSourceKey);
                needClearDsKey = true;
            }

            //优先获取用户自己配置的 dbType
            DbType dbType = DialectFactory.getHintDbType();
            DbType dbTypeGlobal = DialectFactory.getGlobalDbType();
            //当前线程没有设置dbType,但是全局设置了dbTypeGlobal，那么就使用全局的dbTypeGlobal
            if (dbTypeGlobal != null && dbType == null) {
                dbType = dbTypeGlobal;
            }
            if (dbType == null) {
                if (dataSourceKey != null && dataSource != null) {
                    dbType = dataSource.getDbType(dataSourceKey);
                }
                if (dbType == null) {
                    dbType = OrmConfig.getDefaultConfig().getDbType();
                }
                DialectFactory.setHintDbType(dbType);
                needClearDbType = true;
            }
            return method.invoke(mapper, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        } finally {
            if (needClearDbType) {
                DialectFactory.clearHintDbType();
            }
            if (needClearDsKey) {
                DataSourceKey.clear();
            }
        }
    }

    @Nullable
    private static String getConfigDataSourceKey(Method method, Object proxy) {
        UseDataSource useDataSource = method.getAnnotation(UseDataSource.class);
        if (useDataSource != null && StrUtil.isNotBlank(useDataSource.value())) {
            return useDataSource.value();
        }

        Class<?>[] interfaces = proxy.getClass().getInterfaces();
        for (Class<?> anInterface : interfaces) {
            UseDataSource annotation = anInterface.getAnnotation(UseDataSource.class);
            if (annotation != null) {
                return annotation.value();
            }
        }

        if (interfaces[0] != RowMapper.class) {
            TableInfo tableInfo = TableInfoFactory.ofMapperClass(interfaces[0]);
            if (tableInfo != null) {
                String dataSourceKey = tableInfo.getDataSource();
                if (StrUtil.isNotBlank(dataSourceKey)) {
                    return dataSourceKey;
                }
            }
        }
        return null;
    }
}
