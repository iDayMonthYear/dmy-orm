package cn.com.idmy.orm.core.dialect;

import cn.com.idmy.orm.core.query.QueryWrapper;
import cn.com.idmy.orm.core.row.Row;
import cn.com.idmy.orm.core.table.TableInfo;
import cn.com.idmy.orm.core.table.TableManager;

import java.util.List;

/**
 * @author michael
 */
public interface Dialect {
    String wrap(String keyword);

    String wrapColumnAlias(String keyword);

    default String getRealTable(String table) {
        return TableManager.getRealTable(table);
    }

    default String getRealSchema(String schema, String table) {
        return TableManager.getRealSchema(schema, table);
    }

    String forHint(String hintString);

    String forInsertRow(String schema, String tableName, Row row);

    String forInsertBatchWithFirstRowColumns(String schema, String tableName, List<Row> rows);

    String forDeleteById(String schema, String tableName, String[] primaryKeys);

    String forDeleteBatchByIds(String schema, String tableName, String[] primaryKeys, Object[] ids);

    String forDeleteByQuery(QueryWrapper queryWrapper);

    String forUpdateById(String schema, String tableName, Row row);

    String forUpdateByQuery(QueryWrapper queryWrapper, Row data);

    String forUpdateBatchById(String schema, String tableName, List<Row> rows);

    String forSelectOneById(String schema, String tableName, String[] primaryKeys, Object[] primaryValues);

    String forSelectByQuery(QueryWrapper queryWrapper);

    String buildSelectSql(QueryWrapper queryWrapper);

    String buildNoSelectSql(QueryWrapper queryWrapper);

    String buildDeleteSql(QueryWrapper queryWrapper);

    String buildWhereConditionSql(QueryWrapper queryWrapper);

    //////for entity /////
    String forInsertEntity(TableInfo tableInfo, Object entity, boolean ignoreNulls);

    String forInsertEntityWithPk(TableInfo tableInfo, Object entity, boolean ignoreNulls);

    String forInsertEntityBatch(TableInfo tableInfo, List<?> entities);

    String forDeleteEntityById(TableInfo tableInfo);

    String forDeleteEntityBatchByIds(TableInfo tableInfo, Object[] primaryValues);

    String forDeleteEntityBatchByQuery(TableInfo tableInfo, QueryWrapper queryWrapper);

    String forUpdateEntity(TableInfo tableInfo, Object entity, boolean ignoreNulls);

    String forUpdateEntityByQuery(TableInfo tableInfo, Object entity, boolean ignoreNulls, QueryWrapper queryWrapper);

    String forSelectOneEntityById(TableInfo tableInfo);

    String forSelectEntityListByIds(TableInfo tableInfo, Object[] primaryValues);

    /**
     * 权限处理
     *
     * @param queryWrapper queryWrapper
     * @param operateType  操作类型
     */
    default void prepareAuth(QueryWrapper queryWrapper, OperateType operateType) {
    }

    /**
     * 权限处理
     *
     * @param schema      schema
     * @param tableName   表名
     * @param sql         sql
     * @param operateType 操作类型
     */
    default void prepareAuth(String schema, String tableName, StringBuilder sql, OperateType operateType) {
    }

    /**
     * 权限处理
     *
     * @param tableInfo   tableInfo
     * @param sql         sql
     * @param operateType 操作类型
     */
    default void prepareAuth(TableInfo tableInfo, StringBuilder sql, OperateType operateType) {
    }
}
