package cn.com.idmy.orm.ast;

import java.io.Serializable;

@FunctionalInterface
public interface FieldGetter<T, R> extends Serializable {
    R get(T t);
}