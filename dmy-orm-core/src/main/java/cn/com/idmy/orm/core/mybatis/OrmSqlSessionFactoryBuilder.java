package cn.com.idmy.orm.core.mybatis;

import cn.com.idmy.orm.core.OrmGlobalConfig;
import cn.com.idmy.orm.core.exception.OrmExceptions;
import cn.com.idmy.orm.core.row.RowMapper;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

public class OrmSqlSessionFactoryBuilder extends SqlSessionFactoryBuilder {

    @Override
    public SqlSessionFactory build(Reader reader, String environment, Properties properties) {
        try {
            // 需要 mybatis v3.5.13+
            // https://github.com/mybatis/mybatis-3/commit/d7826d71a7005a8b4d4e0e7a020db0f6c7e253a4
            XMLConfigBuilder parser = new XMLConfigBuilder(OrmConfiguration.class, reader, environment, properties);
            return build(parser.parse());
        } catch (Exception e) {
            throw ExceptionFactory.wrapException("Error building SqlSession.", e);
        } finally {
            ErrorContext.instance().reset();
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                // Intentionally ignore. Prefer previous error.
            }
        }
    }


    @Override
    public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
        try {
            // 需要 mybatis v3.5.13+
            // https://github.com/mybatis/mybatis-3/commit/d7826d71a7005a8b4d4e0e7a020db0f6c7e253a4
            XMLConfigBuilder parser = new XMLConfigBuilder(OrmConfiguration.class, inputStream, environment, properties);
            return build(parser.parse());
        } catch (Exception e) {
            throw ExceptionFactory.wrapException("Error building SqlSession.", e);
        } finally {
            ErrorContext.instance().reset();
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                // Intentionally ignore. Prefer previous error.
            }
        }
    }


    @Override
    public SqlSessionFactory build(Configuration configuration) {
        if (!OrmConfiguration.class.isAssignableFrom(configuration.getClass())) {
            throw OrmExceptions.wrap("only support FlexMybatisConfiguration.");
        }

        SqlSessionFactory sessionFactory = super.build(configuration);

        // 设置mybatis的默认mapper,
        initDefaultMappers(configuration);

        // 设置全局配置的 sessionFactory
        initGlobalConfig(configuration, sessionFactory);

        return sessionFactory;
    }

    /**
     * 设置 mybatis-flex 默认的 Mapper
     * 当前只有 RowMapper {@link RowMapper}
     */
    private void initDefaultMappers(Configuration configuration) {
        configuration.addMapper(RowMapper.class);
    }


    /**
     * 设置全局配置
     *
     * @param configuration
     * @param sessionFactory
     */
    private void initGlobalConfig(Configuration configuration, SqlSessionFactory sessionFactory) {
        String environmentId = configuration.getEnvironment().getId();

        OrmGlobalConfig globalConfig = OrmGlobalConfig.getConfig(environmentId);
        boolean configUnInitialize = globalConfig == null;
        if (configUnInitialize) {
            globalConfig = new OrmGlobalConfig();
        }

        globalConfig.setSqlSessionFactory(sessionFactory);
        globalConfig.setConfiguration(configuration);

        boolean isDefault = OrmGlobalConfig.getDefaultConfig() == globalConfig;
        // #I9V9MB 多个SqlSessionFactory初始化时，被最后一个覆盖默认配置
        OrmGlobalConfig.setConfig(environmentId, globalConfig, configUnInitialize || isDefault);
    }


}
