package cn.com.idmy.orm.core;

import java.io.Serializable;

@FunctionalInterface
public interface ColumnGetter<T, R> extends Serializable {
    R get(T t);
}