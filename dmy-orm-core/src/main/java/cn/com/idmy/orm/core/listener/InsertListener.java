package cn.com.idmy.orm.core.listener;

/**
 * 用于监听实体类数据被新增到数据库，可以在实体类被新增时做一些前置操作。
 *
 * @see AbstractInsertListener
 */
@FunctionalInterface
public interface InsertListener extends Listener {

    /**
     * 新增操作的前置操作。
     *
     * @param entity 实体类
     */
    void onInsert(Object entity);

}
