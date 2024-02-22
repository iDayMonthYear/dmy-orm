package cn.com.idmy.orm.core.field;

import cn.com.idmy.orm.core.query.QueryWrapper;

/**
 * 属性查询条件构建。
 *
 * @param <T> 实体类类型
 */
public interface QueryBuilder<T> {

    /**
     * 构建查询属性的 {@link QueryWrapper} 对象。
     *
     * @param entity 实体类
     * @return 查询条件
     */
    QueryWrapper build(T entity);
}
