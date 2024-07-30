package cn.com.idmy.orm.core.util;

import java.io.Serializable;

/**
 * @author michael
 */
@FunctionalInterface
public interface LambdaGetter<T> extends Serializable {

    /**
     * 返回实体类的属性
     *
     * @param source
     * @return 实体类的属性
     */
    Object get(T source);

}

