
package cn.com.idmy.orm.spring;

import cn.com.idmy.orm.core.datasource.OrmDataSource;
import cn.com.idmy.orm.core.transaction.TransactionContext;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * spring 事务支持
 *
 * @author life
 * @author michael
 */
@RequiredArgsConstructor
public class FlexSpringTransaction implements Transaction {
    private final OrmDataSource dataSource;
    private Boolean isConnectionTransactional;
    private Boolean autoCommit;
    private Connection connection;

    @Override
    public Connection getConnection() throws SQLException {
        if (isConnectionTransactional == null) {
            connection = dataSource.getConnection();
            isConnectionTransactional = StrUtil.isNotBlank(TransactionContext.getXID());
            autoCommit = connection.getAutoCommit();
            return connection;
        } else if (isConnectionTransactional) {
            return dataSource.getConnection();
        } else {
            return connection;
        }
    }

    @Override
    public void commit() throws SQLException {
        if (connection != null && !isConnectionTransactional && !autoCommit) {
            connection.commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (connection != null && !isConnectionTransactional && !autoCommit) {
            connection.rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !isConnectionTransactional) {
            connection.close();
        }
    }

    @Nullable
    @Override
    public Integer getTimeout() throws SQLException {
        return null;
    }
}
