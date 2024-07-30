package cn.com.idmy.orm.core;

import cn.com.idmy.orm.annotation.KeyType;
import cn.com.idmy.orm.core.datasource.OrmDataSource;
import cn.com.idmy.orm.core.dialect.DbType;
import cn.com.idmy.orm.core.exception.OrmAssert;
import cn.com.idmy.orm.core.listener.InsertListener;
import cn.com.idmy.orm.core.listener.Listener;
import cn.com.idmy.orm.core.listener.SetListener;
import cn.com.idmy.orm.core.listener.UpdateListener;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 全局配置文件
 */
@Getter
public class OrmGlobalConfig {
    /**
     * 默认使用 Mysql 数据库类型
     */
    @Setter
    private DbType dbType = DbType.MYSQL;

    /**
     * Mybatis 配置
     */
    private Configuration configuration;

    /**
     * 创建好的 sqlSessionFactory
     */
    @Setter
    private SqlSessionFactory sqlSessionFactory;

    /**
     * 全局的 ID 生成策略配置，当 @Id 未配置 或者 配置 KeyType 为 None 时
     * 使用当前全局配置
     */
    @Setter
    private KeyConfig keyConfig;

    /**
     * entity 的监听器
     */
    @Setter
    private Map<Class<?>, List<SetListener>> entitySetListeners = new ConcurrentHashMap<>();
    @Setter
    private Map<Class<?>, List<UpdateListener>> entityUpdateListeners = new ConcurrentHashMap<>();
    @Setter
    private Map<Class<?>, List<InsertListener>> entityInsertListeners = new ConcurrentHashMap<>();

    /**
     * 逻辑删除的相关配置
     */
    private Object normalValueOfLogicDelete = OrmConsts.LOGIC_DELETE_NORMAL;
    private Object deletedValueOfLogicDelete = OrmConsts.LOGIC_DELETE_DELETED;

    /**
     * 分页查询时，默认每页显示的数据数量。
     */
    @Setter
    private int defaultPageSize = 10;

    /**
     * 分页查询时，默认每页显示的数据数量最大限制。
     */
    @Setter
    private int defaultMaxPageSize = Integer.MAX_VALUE;


    /**
     * 默认的 Relation 注解查询深度
     */
    @Setter
    private int defaultRelationQueryDepth = 2;

    /**
     * 默认的逻辑删除字段，允许设置 {@code null} 忽略匹配。
     */
    @Setter
    private String logicDeleteColumn;

    /**
     * 默认的多租户字段，允许设置 {@code null} 忽略匹配。
     */
    @Setter
    private String tenantColumn;

    /**
     * 默认的乐观锁字段，允许设置 {@code null} 忽略匹配。
     */
    @Setter
    private String versionColumn;

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        DataSource dataSource = configuration.getEnvironment().getDataSource();
        if (dataSource instanceof OrmDataSource) {
            this.dbType = ((OrmDataSource) dataSource).getDefaultDbType();
        }
    }

    public void registerSetListener(SetListener listener, Class<?>... classes) {
        for (Class<?> aClass : classes) {
            entitySetListeners.computeIfAbsent(aClass, k -> new ArrayList<>()).add(listener);
        }
    }

    public void registerUpdateListener(UpdateListener listener, Class<?>... classes) {
        for (Class<?> aClass : classes) {
            entityUpdateListeners.computeIfAbsent(aClass, k -> new ArrayList<>()).add(listener);
        }
    }

    public void registerInsertListener(InsertListener listener, Class<?>... classes) {
        for (Class<?> aClass : classes) {
            entityInsertListeners.computeIfAbsent(aClass, k -> new ArrayList<>()).add(listener);
        }
    }

    public List<SetListener> getSetListener(Class<?> entityClass) {
        return entitySetListeners.get(entityClass);
    }

    /**
     * 获取支持该 {@code entityClass} 的set监听器
     * <p>当registerClass是entityClass的本身或其超类时，则视为支持</p>
     *
     * @param entityClass 实体class
     * @return UpdateListener
     */
    public List<SetListener> getSupportedSetListener(Class<?> entityClass) {
        return this.findSupportedListeners(entityClass, this.entitySetListeners);
    }

    public List<UpdateListener> getUpdateListener(Class<?> entityClass) {
        return entityUpdateListeners.get(entityClass);
    }

    /**
     * 查找支持该 {@code entityClass} 的监听器
     *
     * @param entityClass 实体class
     * @param listenerMap 监听器map
     * @param <T>         监听器类型
     * @return 符合条件的监听器
     */
    public <T extends Listener> List<T> findSupportedListeners(Class<?> entityClass, Map<Class<?>, List<T>> listenerMap) {
        return listenerMap.entrySet()
                .stream()
                .filter(entry -> entry.getKey().isAssignableFrom(entityClass))
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.toList());
    }

    /**
     * 获取支持该 {@code entityClass} 的update监听器
     * <p>当registerClass是entityClass的本身或其超类时，则视为支持</p>
     *
     * @param entityClass 实体class
     * @return UpdateListener
     */
    public List<UpdateListener> getSupportedUpdateListener(Class<?> entityClass) {
        return this.findSupportedListeners(entityClass, this.entityUpdateListeners);
    }


    public List<InsertListener> getInsertListener(Class<?> entityClass) {
        return entityInsertListeners.get(entityClass);
    }

    /**
     * 获取支持该 {@code entityClass} 的insert监听器
     * <p>当registerClass是entityClass的本身或其超类时，则视为支持</p>
     *
     * @param entityClass 实体class
     * @return InsertListener
     */
    public List<InsertListener> getSupportedInsertListener(Class<?> entityClass) {
        return this.findSupportedListeners(entityClass, this.entityInsertListeners);
    }

    public void setNormalValueOfLogicDelete(Object normalValueOfLogicDelete) {
        OrmAssert.notNull(normalValueOfLogicDelete, "normalValueOfLogicDelete");
        this.normalValueOfLogicDelete = normalValueOfLogicDelete;
    }

    public void setDeletedValueOfLogicDelete(Object deletedValueOfLogicDelete) {
        OrmAssert.notNull(deletedValueOfLogicDelete, "deletedValueOfLogicDelete");
        this.deletedValueOfLogicDelete = deletedValueOfLogicDelete;
    }

    public OrmDataSource getDataSource() {
        return (OrmDataSource) getConfiguration().getEnvironment().getDataSource();
    }

    public static void setGlobalConfigs(ConcurrentHashMap<String, OrmGlobalConfig> globalConfigs) {
        OrmGlobalConfig.globalConfigs = globalConfigs;
    }

    /**
     * 对应的是 注解 {@link cn.com.idmy.orm.annotation.Id} 的配置
     */
    @Setter
    @Getter
    public static class KeyConfig {
        private KeyType keyType;
        private String value;
        private boolean before = true;
    }

    /////static factory methods/////
    @Getter
    private static ConcurrentHashMap<String, OrmGlobalConfig> globalConfigs = new ConcurrentHashMap<>();
    @Getter
    private static OrmGlobalConfig defaultConfig = new OrmGlobalConfig();

    public static void setDefaultConfig(OrmGlobalConfig config) {
        if (config == null) {
            throw new NullPointerException("config must not be null.");
        }
        defaultConfig = config;
    }

    public static OrmGlobalConfig getConfig(Configuration configuration) {
        return getConfig(configuration.getEnvironment().getId());
    }

    public static OrmGlobalConfig getConfig(String environmentId) {
        return globalConfigs.get(environmentId);
    }


    /**
     * 设置全局配置
     *
     * @param id        环境id
     * @param config    全局配置
     * @param isDefault 自动指定默认全局配置（在多源时，方便由注解指定默认源）
     */
    public static synchronized void setConfig(String id, OrmGlobalConfig config, boolean isDefault) {
        if (isDefault) {
            defaultConfig.setSqlSessionFactory(config.sqlSessionFactory);
            defaultConfig.setConfiguration(config.configuration);
        }

        globalConfigs.put(id, isDefault ? defaultConfig : config);
    }

}
