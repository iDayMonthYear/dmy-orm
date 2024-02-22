
package cn.com.idmy.orm.core.listener;

/**
 * 查询实体类数据时，对实体类的属性设置的监听。
 */
public interface SetListener extends Listener {

    /**
     * 实体类属性设置。
     *
     * @param entity   实体类
     * @param property 属性名
     * @param value    属性值
     * @return 属性值
     */
    Object onSet(Object entity, String property, Object value);

}
