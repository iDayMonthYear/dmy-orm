package cn.com.idmy.orm.core.mybatis.binding;

import cn.com.idmy.orm.annotation.UseDataSource;
import cn.com.idmy.orm.core.OrmGlobalConfig;
import cn.com.idmy.orm.core.datasource.DataSourceKey;
import cn.com.idmy.orm.core.datasource.OrmDataSource;
import cn.com.idmy.orm.core.dialect.DbType;
import cn.com.idmy.orm.core.dialect.DialectFactory;
import cn.com.idmy.orm.core.mybatis.OrmConfiguration;
import cn.com.idmy.orm.core.row.RowMapper;
import cn.com.idmy.orm.core.table.TableInfo;
import cn.com.idmy.orm.core.table.TableInfoFactory;
import cn.com.idmy.orm.core.util.StringUtil;
import jakarta.annotation.Nullable;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Method;
import java.util.Map;

public class MapperProxy<T> extends MybatisMapperProxy<T> {
    private final OrmDataSource dataSource;

    public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface, Map<Method, MapperMethodInvoker> methodCache,
                       OrmConfiguration configuration) {
        super(sqlSession, mapperInterface, methodCache);
        this.dataSource = (OrmDataSource) configuration.getEnvironment().getDataSource();
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }

        boolean needClearDsKey = false;
        boolean needClearDbType = false;

        //由用户指定的数据
        String userDsKey = DataSourceKey.get();
        //最终使用的数据源
        String finalDsKey = userDsKey;

        try {
            if (StringUtil.isBlank(finalDsKey)) {
                finalDsKey = getMethodDsKey(method, proxy);
            }

            //通过自定义分配策略去获取最终的数据源
            finalDsKey = DataSourceKey.getShardingDsKey(finalDsKey, proxy, method, args);

            if (StringUtil.isNotBlank(finalDsKey) && !finalDsKey.equals(userDsKey)) {
                needClearDsKey = true;
                DataSourceKey.use(finalDsKey);
            }

            DbType hintDbType = DialectFactory.getHintDbType();
            if (hintDbType == null) {
                if (finalDsKey != null && dataSource != null) {
                    hintDbType = dataSource.getDbType(finalDsKey);
                }

                if (hintDbType == null) {
                    hintDbType = OrmGlobalConfig.getDefaultConfig().getDbType();
                }

                needClearDbType = true;
                DialectFactory.setHintDbType(hintDbType);
            }
            return cachedInvoker(method).invoke(proxy, method, args, sqlSession);
        } catch (Throwable e) {
            throw ExceptionUtil.unwrapThrowable(e);
        } finally {
            if (needClearDbType) {
                DialectFactory.clearHintDbType();
            }
            if (needClearDsKey) {
                if (userDsKey != null) {
                    //恢复用户设置的数据源，并由用户主动去清除
                    DataSourceKey.use(userDsKey);
                } else {
                    DataSourceKey.clear();
                }
            }
        }
    }

    @Nullable
    private static String getMethodDsKey(Method method, Object proxy) {
        UseDataSource methodAnno = method.getAnnotation(UseDataSource.class);
        if (methodAnno != null && StringUtil.isNotBlank(methodAnno.value())) {
            return methodAnno.value();
        }

        Class<?>[] interfaces = proxy.getClass().getInterfaces();
        for (Class<?> anInterface : interfaces) {
            UseDataSource classAnno = anInterface.getAnnotation(UseDataSource.class);
            if (classAnno != null && StringUtil.isNotBlank(classAnno.value())) {
                return classAnno.value();
            }
        }

        if (interfaces[0] != RowMapper.class) {
            TableInfo tableInfo = TableInfoFactory.ofMapperClass(interfaces[0]);
            if (tableInfo != null) {
                String tableDsKey = tableInfo.getDataSource();
                if (StringUtil.isNotBlank(tableDsKey)) {
                    return tableDsKey;
                }
            }
        }
        return null;
    }

}
