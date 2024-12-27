package cn.com.idmy.orm.core;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

@FunctionalInterface
public interface FieldGetter<IN, OUT> extends Serializable {
    @NotNull
    OUT get(@NotNull IN in);
}