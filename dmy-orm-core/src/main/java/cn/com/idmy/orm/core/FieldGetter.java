package cn.com.idmy.orm.core;

import java.io.Serializable;

@FunctionalInterface
public interface FieldGetter<IN, OUT> extends Serializable {
    OUT get(IN in);
}