package cn.com.idmy.orm.core.datasource;

import java.lang.reflect.Method;

public interface DataSourceShardingStrategy {
    String doSharding(String currentDataSourceKey, Object mapper, Method mapperMethod, Object[] methodArgs);
}
