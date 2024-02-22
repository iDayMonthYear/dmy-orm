package cn.com.idmy.orm.core.mybatis;

import cn.com.idmy.orm.core.transaction.TransactionContext;
import jakarta.annotation.Nullable;
import lombok.experimental.Delegate;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetWrapper;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandler;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * @author michael
 * 用于增强对 Cursor 查询处理，以及 List<String> 的自动映射问题
 */
public class OrmResultSetHandler extends OrmDefaultResultSetHandler {

    public OrmResultSetHandler(Executor executor, MappedStatement mappedStatement, ParameterHandler parameterHandler
            , ResultHandler<?> resultHandler, BoundSql boundSql, RowBounds rowBounds) {
        super(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
    }


    /**
     * 从写 handleCursorResultSets, 用于适配在事务下自动关闭 Cursor
     */
    @Override
    public <E> Cursor<E> handleCursorResultSets(Statement stmt) throws SQLException {
        Cursor<E> defaultCursor = super.handleCursorResultSets(stmt);

        //in transaction
        if (TransactionContext.getXID() != null) {
            return new FlexCursor<>(defaultCursor);
        }

        return defaultCursor;
    }


    /**
     * 修复当实体类中存在 List<String> 或者 List<Integer> 等自动映射出错的问题
     * 本质问题应该出现 mybatis 判断有误
     * <p>
     * https://gitee.com/mybatis-flex/mybatis-flex/issues/I7XBQS
     * https://gitee.com/mybatis-flex/mybatis-flex/issues/I7X7G7
     *
     * @param rsw
     * @param resultMap
     * @param columnPrefix
     * @throws SQLException
     */
    @Nullable
    @Override
    protected Object createPrimitiveResultObject(ResultSetWrapper rsw, ResultMap resultMap, String columnPrefix)
            throws SQLException {
        final Class<?> resultType = resultMap.getType();
        if (!resultMap.getResultMappings().isEmpty()) {
            final List<ResultMapping> resultMappingList = resultMap.getResultMappings();
            final ResultMapping mapping = resultMappingList.get(0);
            String columnName = prependPrefix(mapping.getColumn(), columnPrefix);
            TypeHandler<?> typeHandler = mapping.getTypeHandler();

            Collection<String> mappedColumnNames = rsw.getMappedColumnNames(resultMap, columnPrefix);
            if (columnName != null && mappedColumnNames.contains(columnName.toUpperCase(Locale.ENGLISH))) {
                return typeHandler.getResult(rsw.getResultSet(), columnName);
            } else {
                return null;
            }
        } else {
            String columnName = rsw.getColumnNames().get(0);
            TypeHandler<?> typeHandler = rsw.getTypeHandler(resultType, columnName);
            return typeHandler.getResult(rsw.getResultSet(), columnName);
        }
    }

    static class FlexCursor<T> implements Cursor<T> {
        @Delegate
        private final Cursor<T> originalCursor;

        public FlexCursor(Cursor<T> cursor) {
            this.originalCursor = cursor;
            TransactionContext.holdCursor(cursor);
        }

        @Override
        public void close() {
            // 由 TransactionContext 去关闭
        }
    }
}
