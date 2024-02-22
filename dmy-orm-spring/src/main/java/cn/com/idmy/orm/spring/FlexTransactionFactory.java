
package cn.com.idmy.orm.spring;

import cn.com.idmy.orm.core.datasource.OrmDataSource;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author life
 * @author michael
 */
public class FlexTransactionFactory extends SpringManagedTransactionFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new FlexSpringTransaction((OrmDataSource) dataSource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Transaction newTransaction(Connection conn) {
        throw new UnsupportedOperationException("New Flex transactions require a DataSource");
    }
}
