

package cn.com.idmy.orm.core.listener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 类型支持 update 监听器。
 *
 * @author snow
 * @author robot.luo
 * @since 2023/4/28
 */
public abstract class AbstractUpdateListener<T> implements UpdateListener {

    /**
     * 支持的类型
     */
    private final Class<T> supportType;

    @SuppressWarnings("unchecked")
    protected AbstractUpdateListener() {
        Type[] params = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
        if (params.length == 0) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + "继承AbstractUpdateListener请指定泛型");
        } else {
            supportType = (Class<T>) params[0];
        }
    }

    /**
     * 更新操作的前置操作。
     *
     * @param entity 实体类
     */
    public abstract void doUpdate(T entity);

    @Override
    @SuppressWarnings("unchecked")
    public void onUpdate(Object entity) {
        if (supportType.isInstance(entity)) {
            T object = (T) entity;
            doUpdate(object);
        }
    }
}
