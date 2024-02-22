package cn.com.idmy.orm.core.datasource;

import cn.com.idmy.orm.core.dialect.DbType;
import cn.com.idmy.orm.core.dialect.DbTypeUtil;
import cn.com.idmy.orm.core.transaction.TransactionContext;
import cn.com.idmy.orm.core.transaction.TransactionalManager;
import cn.com.idmy.orm.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author michael
 */
@Getter
@Slf4j
public class OrmDataSource extends AbstractDataSource {
    private static final char LOAD_BALANCE_KEY_SUFFIX = '*';
    private final Map<String, DataSource> dataSources = new HashMap<>();
    private final Map<String, DbType> dbTypes = new HashMap<>();
    private final DbType defaultDbType;
    private final String defaultDataSourceKey;
    private final DataSource defaultDataSource;

    public OrmDataSource(String dataSourceKey, DataSource dataSource) {
        this(dataSourceKey, dataSource, true);
    }

    public OrmDataSource(String dataSourceKey, DataSource dataSource, boolean needDecryptDataSource) {
        if (needDecryptDataSource) {
            DataSourceManager.decryptDataSource(dataSource);
        }

        this.defaultDataSourceKey = dataSourceKey;
        this.defaultDataSource = dataSource;
        this.defaultDbType = DbTypeUtil.getDbType(dataSource);

        dataSources.put(dataSourceKey, dataSource);
        dbTypes.put(dataSourceKey, defaultDbType);
    }

    public void addDataSource(String dataSourceKey, DataSource dataSource) {
        addDataSource(dataSourceKey, dataSource, true);
    }

    public void addDataSource(String dataSourceKey, DataSource dataSource, boolean needDecryptDataSource) {
        if (needDecryptDataSource) {
            DataSourceManager.decryptDataSource(dataSource);
        }
        dataSources.put(dataSourceKey, dataSource);
        dbTypes.put(dataSourceKey, DbTypeUtil.getDbType(dataSource));
    }

    public void removeDatasource(String dataSourceKey) {
        dataSources.remove(dataSourceKey);
        dbTypes.remove(dataSourceKey);
    }

    public DbType getDbType(String dataSourceKey) {
        return dbTypes.get(dataSourceKey);
    }

    @Override
    public Connection getConnection() throws SQLException {
        String xid = TransactionContext.getXID();
        if (StrUtil.isNotBlank(xid)) {
            String dataSourceKey = DataSourceKey.get();
            if (StrUtil.isBlank(dataSourceKey)) {
                dataSourceKey = defaultDataSourceKey;
            }

            Connection connection = TransactionalManager.getConnection(xid, dataSourceKey);
            if (connection == null) {
                connection = proxy(getDataSource().getConnection(), xid);
                TransactionalManager.hold(xid, dataSourceKey, connection);
            }
            return connection;
        } else {
            return getDataSource().getConnection();
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        String xid = TransactionContext.getXID();
        if (StrUtil.isNotBlank(xid)) {
            String dataSourceKey = DataSourceKey.get();
            if (StrUtil.isBlank(dataSourceKey)) {
                dataSourceKey = defaultDataSourceKey;
            }
            Connection connection = TransactionalManager.getConnection(xid, dataSourceKey);
            if (connection == null) {
                connection = proxy(getDataSource().getConnection(username, password), xid);
                TransactionalManager.hold(xid, dataSourceKey, connection);
            }
            return connection;
        } else {
            return getDataSource().getConnection(username, password);
        }
    }

    static void closeAutoCommit(Connection connection) {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            if (log.isDebugEnabled()) {
                log.debug("Error set autoCommit to false. Cause: " + e);
            }
        }
    }

    static void resetAutoCommit(Connection connection) {
        try {
            if (!connection.getAutoCommit()) {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            if (log.isDebugEnabled()) {
                log.debug("Error resetting autoCommit to true before closing the connection. " +
                        "Cause: " + e);
            }
        }
    }

    public Connection proxy(Connection connection, String xid) {
        return (Connection) Proxy.newProxyInstance(OrmDataSource.class.getClassLoader()
                , new Class[]{Connection.class}
                , new ConnectionHandler(connection, xid));
    }

    /**
     * 方便用于 {@link DbTypeUtil#getDbType(DataSource)}
     */
    public String getUrl() {
        return DbTypeUtil.getJdbcUrl(defaultDataSource);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return (T) this;
        }
        return getDataSource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> clazz) throws SQLException {
        return (clazz.isInstance(this) || getDataSource().isWrapperFor(clazz));
    }

    private DataSource getDataSource() {
        DataSource dataSource = defaultDataSource;
        if (dataSources.size() > 1) {
            String dataSourceKey = DataSourceKey.get();
            if (StrUtil.isNotBlank(dataSourceKey)) {
                //负载均衡 key
                if (dataSourceKey.charAt(dataSourceKey.length() - 1) == LOAD_BALANCE_KEY_SUFFIX) {
                    List<String> matchedKeys = getMatchedKeys(dataSourceKey);
                    String randomKey = matchedKeys.get(ThreadLocalRandom.current().nextInt(matchedKeys.size()));
                    return dataSources.get(randomKey);
                }
                //非负载均衡 key
                else {
                    dataSource = dataSources.get(dataSourceKey);
                    if (dataSource == null) {
                        throw new IllegalStateException("Cannot get target dataSource by key: \"" + dataSourceKey + "\"");
                    }
                }
            }
        }
        return dataSource;
    }

    private List<String> getMatchedKeys(String dataSourceKey) {
        String prefix = dataSourceKey.substring(0, dataSourceKey.length() - 1);
        List<String> matchedKeys = new ArrayList<>();
        for (String key : dataSources.keySet()) {
            if (key.startsWith(prefix)) {
                matchedKeys.add(key);
            }
        }

        if (matchedKeys.isEmpty()) {
            throw new IllegalStateException("Can not matched dataSource by key: \"" + dataSourceKey + "\"");
        } else {
            return matchedKeys;
        }
    }

    private static class ConnectionHandler implements InvocationHandler {
        private static final String[] proxyMethods = new String[]{"commit", "rollback", "close", "setAutoCommit"};
        private final Connection original;
        private final String xid;

        public ConnectionHandler(Connection original, String xid) {
            closeAutoCommit(original);
            this.original = original;
            this.xid = xid;
        }

        @Nullable
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (ArrayUtil.contains(proxyMethods, method.getName()) && isTransactional()) {
                return null;
            }

            //setAutoCommit: true
            if ("close".equalsIgnoreCase(method.getName())) {
                resetAutoCommit(original);
            }

            return method.invoke(original, args);
        }

        private boolean isTransactional() {
            return Objects.equals(xid, TransactionContext.getXID());
        }
    }
}
