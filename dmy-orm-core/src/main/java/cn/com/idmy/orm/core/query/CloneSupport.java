package cn.com.idmy.orm.core.query;

import java.io.Serializable;

/**
 * <p>克隆支持接口。
 *
 * <p>支持序列化克隆与 {@link Object#clone()} 方法。
 *
 * @param <T> 克隆对象类型
 * @author 王帅
 * @since 2023-06-10
 */
public interface CloneSupport<T> extends Serializable, Cloneable {

    /**
     * 改写 {@link Object#clone()} 方法。
     *
     * @return 克隆对象
     */
    T clone();

}
