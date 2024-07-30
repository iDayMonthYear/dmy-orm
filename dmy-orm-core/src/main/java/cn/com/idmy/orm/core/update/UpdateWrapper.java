package cn.com.idmy.orm.core.update;

import cn.com.idmy.orm.core.query.QueryColumn;
import cn.com.idmy.orm.core.query.QueryCondition;
import cn.com.idmy.orm.core.query.QueryWrapper;
import cn.com.idmy.orm.core.util.LambdaGetter;
import cn.com.idmy.orm.core.util.LambdaUtil;
import cn.com.idmy.orm.core.util.UpdateEntity;
import org.apache.ibatis.javassist.util.proxy.ProxyObject;

import java.io.Serializable;
import java.util.Map;

/**
 * @author michael
 */
public interface UpdateWrapper<T> extends PropertySetter<UpdateWrapper<T>>, Serializable {

    default Map<String, Object> getUpdates() {
        ModifyAttrsRecordHandler handler = (ModifyAttrsRecordHandler) ((ProxyObject) this).getHandler();
        return handler.getUpdates();
    }

    @Override
    default UpdateWrapper<T> set(String property, Object value, boolean isEffective) {
        if (isEffective) {
            if (value instanceof QueryWrapper
                    || value instanceof QueryColumn
                    || value instanceof QueryCondition) {
                getUpdates().put(property, new RawValue(value));
            } else {
                getUpdates().put(property, value);
            }
        }
        return this;
    }

    @Override
    default UpdateWrapper<T> set(QueryColumn property, Object value, boolean isEffective) {
        if (isEffective) {
            if (value instanceof QueryWrapper
                    || value instanceof QueryColumn
                    || value instanceof QueryCondition) {
                getUpdates().put(property.getName(), new RawValue(value));
            } else {
                getUpdates().put(property.getName(), value);
            }
        }
        return this;
    }

    @Override
    default <E> UpdateWrapper<T> set(LambdaGetter<E> property, Object value, boolean isEffective) {
        if (isEffective) {
            if (value instanceof QueryWrapper
                    || value instanceof QueryColumn
                    || value instanceof QueryCondition) {
                getUpdates().put(LambdaUtil.getFieldName(property), new RawValue(value));
            } else {
                getUpdates().put(LambdaUtil.getFieldName(property), value);
            }
        }
        return this;
    }

    @Override
    default UpdateWrapper<T> setRaw(String property, Object value, boolean isEffective) {
        if (isEffective) {
            getUpdates().put(property, new RawValue(value));
        }
        return this;
    }


    @Override
    default UpdateWrapper<T> setRaw(QueryColumn property, Object value, boolean isEffective) {
        if (isEffective) {
            getUpdates().put(property.getName(), new RawValue(value));
        }
        return this;
    }

    @Override
    default <E> UpdateWrapper<T> setRaw(LambdaGetter<E> property, Object value, boolean isEffective) {
        if (isEffective) {
            getUpdates().put(LambdaUtil.getFieldName(property), new RawValue(value));
        }
        return this;
    }


    static <T> UpdateWrapper<T> of(T entity) {
        if (entity instanceof UpdateWrapper) {
            return (UpdateWrapper<T>) entity;
        } else {
            return (UpdateWrapper<T>) UpdateEntity.ofNotNull(entity);
        }
    }

    static <T> UpdateWrapper<T> of(Class<T> tClass) {
        return (UpdateWrapper<T>) UpdateEntity.of(tClass);
    }


    default T toEntity() {
        return (T) this;
    }

}
