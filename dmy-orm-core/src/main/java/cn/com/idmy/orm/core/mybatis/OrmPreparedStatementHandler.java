package cn.com.idmy.orm.core.mybatis;

import cn.com.idmy.orm.core.keygen.IMultiKeyGenerator;
import cn.com.idmy.orm.core.util.ArrayUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.statement.PreparedStatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.*;

/**
 * @author Michael Yang(fuhai999@gmail.com)
 */
public class OrmPreparedStatementHandler extends PreparedStatementHandler {

    public OrmPreparedStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        super(executor, mappedStatement, parameter, rowBounds, resultHandler, boundSql);
    }


    @Override
    protected Statement instantiateStatement(Connection connection) throws SQLException {
        String sql = boundSql.getSql();
        KeyGenerator keyGenerator = mappedStatement.getKeyGenerator();
        if (keyGenerator instanceof Jdbc3KeyGenerator) {
            String[] keyColumnNames = mappedStatement.getKeyColumns();
            if (keyColumnNames == null) {
                return connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            } else {
                return connection.prepareStatement(sql, keyColumnNames);
            }
        }
        // 多主键的场景
        else if (keyGenerator instanceof IMultiKeyGenerator) {
            if (((IMultiKeyGenerator) keyGenerator).hasGeneratedKeys()) {
                String[] keyColumnNames = ((IMultiKeyGenerator) keyGenerator).getKeyColumnNames();
                if (ArrayUtil.isNotEmpty(keyColumnNames)) {
                    return connection.prepareStatement(sql, keyColumnNames);
                } else {
                    return connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                }
            }
        }

        if (mappedStatement.getResultSetType() == ResultSetType.DEFAULT) {
            return connection.prepareStatement(sql);
        } else {
            return connection.prepareStatement(sql, mappedStatement.getResultSetType().getValue(), ResultSet.CONCUR_READ_ONLY);
        }
    }


}
