package cn.com.idmy.orm.core.logicdelete;

import cn.com.idmy.orm.core.dialect.Dialect;
import cn.com.idmy.orm.core.query.QueryWrapper;
import cn.com.idmy.orm.core.table.TableInfo;

/**
 * 逻辑删除处理器。
 */
public interface LogicDeleteProcessor {

    /**
     * 用户构建查询正常数据的条件。
     *
     * @param logicColumn 逻辑删除列
     * @param tableInfo   表信息
     * @param dialect     数据库方言
     */
    String buildLogicNormalCondition(String logicColumn, TableInfo tableInfo, Dialect dialect);

    /**
     * 用户与构建删除数据时的内容。
     *
     * @param logicColumn 逻辑删除列
     * @param tableInfo   表信息
     * @param dialect     数据库方言
     */
    String buildLogicDeletedSet(String logicColumn, TableInfo tableInfo, Dialect dialect);

    /**
     * 用于构建通过 {@link QueryWrapper} 查询数据时的内容。
     *
     * @param queryWrapper   条件构造器
     * @param tableInfo      表信息
     * @param joinTableAlias join table 的别名
     */
    void buildQueryCondition(QueryWrapper queryWrapper, TableInfo tableInfo, String joinTableAlias);

    /**
     * 获取逻辑删除列未删除标记值。
     *
     * @return 未删除标记值
     */
    Object getLogicNormalValue();

    /**
     * 获取逻辑删除列删除时标记值。
     *
     * @return 删除时标记值
     */
    Object getLogicDeletedValue();

}


