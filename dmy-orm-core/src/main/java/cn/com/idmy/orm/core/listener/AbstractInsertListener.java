package cn.com.idmy.orm.core.listener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 类型支持 insert 监听器。
 *
 * @author snow
 * @author robot.luo
 * @since 2023/4/28
 */
public abstract class AbstractInsertListener<T> implements InsertListener {
    /**
     * 支持的类型
     */
    private final Class<T> supportType;

    @SuppressWarnings("unchecked")
    protected AbstractInsertListener() {
        Type[] params = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
        if (params.length == 0) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + "继承AbstractInsertListener请指定泛型");
        } else {
            supportType = (Class<T>) params[0];
        }
    }

    /**
     * 新增操作的前置操作。
     *
     * @param entity 实体类
     */
    public abstract void doInsert(T entity);

    @Override
    @SuppressWarnings("unchecked")
    public void onInsert(Object entity) {
        if (supportType.isInstance(entity)) {
            T object = (T) entity;
            doInsert(object);
        }
    }
}
