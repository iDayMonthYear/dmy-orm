package cn.com.idmy.orm.core;

import cn.com.idmy.orm.core.datasource.OrmDataSource;
import cn.com.idmy.orm.core.exception.OrmAssert;
import cn.com.idmy.orm.core.mybatis.Mappers;
import cn.com.idmy.orm.core.mybatis.OrmConfiguration;
import cn.com.idmy.orm.core.mybatis.OrmSqlSessionFactoryBuilder;
import lombok.Getter;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class OrmBootstrap {
    protected final AtomicBoolean started = new AtomicBoolean(false);

    @Getter
    protected String environmentId = OrmConsts.NAME;
    @Getter
    protected TransactionFactory transactionFactory;

    protected OrmDataSource dataSource;
    @Getter
    protected Configuration configuration;
    @Getter
    protected List<Class<?>> mappers;

    @Getter
    protected Class<? extends Log> logImpl;


    /**
     * 虽然提供了 getInstance，但也允许用户进行实例化，
     * 用于创建多个 MybatisFlexBootstrap 实例达到管理多数据源的目的
     */
    public OrmBootstrap() {

    }

    private static volatile OrmBootstrap instance;

    public static OrmBootstrap getInstance() {
        if (instance == null) {
            synchronized (OrmBootstrap.class) {
                if (instance == null) {
                    instance = new OrmBootstrap();
                }
            }
        }
        return instance;
    }


    public <T> OrmBootstrap addMapper(Class<T> type) {
        if (this.mappers == null) {
            mappers = new ArrayList<>();
        }
        mappers.add(type);
        return this;
    }


    public OrmBootstrap start() {
        if (started.compareAndSet(false, true)) {

            OrmAssert.notNull(dataSource, "dataSource");

            //init configuration
            if (configuration == null) {

                if (transactionFactory == null) {
                    transactionFactory = new JdbcTransactionFactory();
                }

                Environment environment = new Environment(environmentId, transactionFactory, dataSource);
                configuration = new OrmConfiguration(environment);
            }

            if (logImpl != null) {
                configuration.setLogImpl(logImpl);
            }

            //init sqlSessionFactory
            new OrmSqlSessionFactoryBuilder().build(configuration);

            //init mappers
            if (mappers != null) {
                mappers.forEach(configuration::addMapper);
            }

            LogFactory.getLog(OrmBootstrap.class).debug("Mybatis-Flex has started.");
        }

        return this;
    }


    /**
     * 直接获取 mapper 对象执行
     *
     * @param mapperClass
     * @return mapperObject
     */
    public <T> T getMapper(Class<T> mapperClass) {
        return Mappers.ofMapperClass(mapperClass);
    }


    public OrmBootstrap setEnvironmentId(String environmentId) {
        this.environmentId = environmentId;
        return this;
    }

    public OrmBootstrap setTransactionFactory(TransactionFactory transactionFactory) {
        this.transactionFactory = transactionFactory;
        return this;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public OrmBootstrap setDataSource(DataSource dataSource) {
        this.dataSource = new OrmDataSource(OrmConsts.NAME, dataSource);
        return this;
    }

    public OrmBootstrap setDataSource(String dataSourceKey, DataSource dataSource) {
        this.dataSource = new OrmDataSource(dataSourceKey, dataSource);
        return this;
    }

    public OrmBootstrap addDataSource(String dataSourceKey, DataSource dataSource) {
        if (this.dataSource == null) {
            this.dataSource = new OrmDataSource(dataSourceKey, dataSource);
        } else {
            this.dataSource.addDataSource(dataSourceKey, dataSource);
        }
        return this;
    }

    public OrmBootstrap setConfiguration(OrmConfiguration configuration) {
        this.configuration = configuration;
        this.environmentId = configuration.getEnvironment().getId();
        return this;
    }

    public OrmBootstrap setLogImpl(Class<? extends Log> logImpl) {
        this.logImpl = logImpl;
        return this;
    }
}
