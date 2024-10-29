

package cn.com.idmy.orm.spring.datasource;


import cn.com.idmy.orm.annotation.UseDataSource;
import cn.com.idmy.orm.core.datasource.DataSourceKey;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.core.MethodClassKey;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 多数据源切换拦截器。
 *
 * @author 王帅
 * @author barql
 * @author michael
 * @since 2023-06-25
 */
public class DataSourceInterceptor implements MethodInterceptor {
    /**
     * 缓存方法对应的数据源。
     */
    private final Map<Object, String> dataSourceCache = new ConcurrentHashMap<>();

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String dsKey = DataSourceKey.getByManual();
        if (StrUtil.isNotBlank(dsKey)) {
            return invocation.proceed();
        }

        dsKey = findDataSourceKey(invocation.getMethod(), Objects.requireNonNull(invocation.getThis()).getClass());
        if (StrUtil.isBlank(dsKey)) {
            return invocation.proceed();
        }

        //方法嵌套时，挂起的 key
        String suspendKey = DataSourceKey.getByAnnotation();

        try {
            DataSourceKey.useWithAnnotation(dsKey);
            return invocation.proceed();
        } finally {
            //恢复挂起的 key
            if (suspendKey != null) {
                DataSourceKey.useWithAnnotation(suspendKey);
            } else {
                DataSourceKey.clear();
            }
        }
    }

    private String findDataSourceKey(Method method, Class<?> targetClass) {
        Object cacheKey = new MethodClassKey(method, targetClass);
        String dsKey = this.dataSourceCache.get(cacheKey);
        if (dsKey == null) {
            dsKey = determineDataSourceKey(method, targetClass);
            this.dataSourceCache.put(cacheKey, dsKey);
        }
        return dsKey;
    }


    private String determineDataSourceKey(Method method, Class<?> targetClass) {

        // 方法上定义有 UseDataSource 注解
        UseDataSource annotation = method.getAnnotation(UseDataSource.class);
        if (annotation != null) {
            return annotation.value();
        }

        // 类上定义有 UseDataSource 注解
        annotation = targetClass.getAnnotation(UseDataSource.class);
        if (annotation != null) {
            return annotation.value();
        }

        // 接口上定义有 UseDataSource 注解
        Class<?>[] interfaces = targetClass.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            annotation = anInterface.getAnnotation(UseDataSource.class);
            if (annotation != null) {
                return annotation.value();
            }
        }

        return "";
    }

}
