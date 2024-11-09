package cn.com.idmy.orm.core.query.ast;

import java.io.Serializable;

@FunctionalInterface
public interface FieldGetter<T, R> extends Serializable {
    R get(T t);
}