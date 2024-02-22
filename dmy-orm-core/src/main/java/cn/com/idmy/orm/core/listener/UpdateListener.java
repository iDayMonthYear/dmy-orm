
package cn.com.idmy.orm.core.listener;


/**
 * 用于监听实体类数据被更新到数据库，可以在数据被更新的时候，设置一些默认数据。
 *
 * @see AbstractUpdateListener
 */
@FunctionalInterface
public interface UpdateListener extends Listener {

    /**
     * 更新操作的前置操作。
     *
     * @param entity 实体类
     */
    void onUpdate(Object entity);

}
