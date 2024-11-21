package cn.com.idmy.orm.core;

import java.io.Serializable;

@FunctionalInterface
public interface ColumnGetter<IN, OUT> extends Serializable {
    OUT get(IN in);
}