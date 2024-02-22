package cn.com.idmy.orm.core;

import cn.com.idmy.orm.annotation.KeyType;
import cn.com.idmy.orm.core.datasource.OrmDataSource;
import cn.com.idmy.orm.core.dialect.DbType;
import cn.com.idmy.orm.core.listener.InsertListener;
import cn.com.idmy.orm.core.listener.Listener;
import cn.com.idmy.orm.core.listener.SetListener;
import cn.com.idmy.orm.core.listener.UpdateListener;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 全局配置文件
 */
@Getter
@Setter
public class OrmConfig {
    private DbType dbType = DbType.MYSQL;
    private Configuration configuration;
    private SqlSessionFactory sqlSessionFactory;
    /**
     * 全局的 ID 生成策略配置，当 @Id 未配置 或者 配置 KeyType 为 None 时
     * 使用当前全局配置
     */
    private KeyConfig keyConfig;
    private Map<Class<?>, SetListener> entitySetListeners = new ConcurrentHashMap<>();
    private Map<Class<?>, UpdateListener> entityUpdateListeners = new ConcurrentHashMap<>();
    private Map<Class<?>, InsertListener> entityInsertListeners = new ConcurrentHashMap<>();
    private Object normalValueOfLogicDelete = OrmConsts.LOGIC_DELETE_NORMAL;
    private Object deletedValueOfLogicDelete = OrmConsts.LOGIC_DELETE_DELETED;
    private String logicDeleteColumn;
    private int defaultPageSize = 10;
    private String tenantColumn;
    private String versionColumn;

    public void setConfiguration(Configuration cfg) {
        this.configuration = cfg;
        var dataSource = cfg.getEnvironment().getDataSource();
        if (dataSource instanceof OrmDataSource ds) {
            this.dbType = ds.getDefaultDbType();
        }
    }

    public void registerListener(SetListener listener, Class<?>... classes) {
        Arrays.stream(classes).forEach(c -> entitySetListeners.put(c, listener));
    }

    public void registerListener(UpdateListener listener, Class<?>... classes) {
        Arrays.stream(classes).forEach(c -> entityUpdateListeners.put(c, listener));
    }

    public void registerListener(InsertListener listener, Class<?>... classes) {
        Arrays.stream(classes).forEach(c -> entityInsertListeners.put(c, listener));
    }

    public SetListener getSetListener(Class<?> entityClass) {
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

    public UpdateListener getUpdateListener(Class<?> entityClass) {
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
    public <T extends Listener> List<T> findSupportedListeners(Class<?> entityClass, Map<Class<?>, T> listenerMap) {
        return listenerMap.entrySet().stream().filter(entry -> entry.getKey().isAssignableFrom(entityClass)).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    /**
     * 获取支持该 {@code entityClass} 的update监听器
     * <p>当registerClass是entityClass的本身或其超类时，则视为支持</p>
     *
     * @param entityClass 实体class
     * @return UpdateListener
     */
    public List<UpdateListener> findSupportedUpdateListener(Class<?> entityClass) {
        return findSupportedListeners(entityClass, this.entityUpdateListeners);
    }

    public InsertListener getInsertListener(Class<?> entityClass) {
        return entityInsertListeners.get(entityClass);
    }

    /**
     * 获取支持该 {@code entityClass} 的insert监听器
     * <p>当registerClass是entityClass的本身或其超类时，则视为支持</p>
     *
     * @param entityClass 实体class
     * @return InsertListener
     */
    public List<InsertListener> findSupportedInsertListener(Class<?> entityClass) {
        return findSupportedListeners(entityClass, this.entityInsertListeners);
    }

    public OrmDataSource getDataSource() {
        return (OrmDataSource) getConfiguration().getEnvironment().getDataSource();
    }

    /**
     * 对应的是 注解 {@link cn.com.idmy.orm.annotation.Id} 的配置
     */
    @Getter
    @Setter
    public static class KeyConfig {
        private KeyType keyType;
        private String value;
        private boolean before = true;
    }

    @Getter
    @Setter
    private static ConcurrentHashMap<String, OrmConfig> globalConfigs = new ConcurrentHashMap<>();
    @Getter
    @Setter
    private static OrmConfig defaultConfig = new OrmConfig();

    public static OrmConfig getConfig(Configuration cfg) {
        return getConfig(cfg.getEnvironment().getId());
    }

    public static OrmConfig getConfig(String envId) {
        return globalConfigs.get(envId);
    }


    /**
     * 设置全局配置
     *
     * @param envId     环境id
     * @param config    全局配置
     * @param isDefault 自动指定默认全局配置（在多源时，方便由注解指定默认源）
     */
    public static synchronized void setConfig(String envId, OrmConfig config, boolean isDefault) {
        if (isDefault) {
            defaultConfig.setSqlSessionFactory(config.sqlSessionFactory);
            defaultConfig.setConfiguration(config.configuration);
        }
        globalConfigs.put(envId, isDefault ? defaultConfig : config);
    }
}
