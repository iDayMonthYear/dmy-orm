package cn.com.idmy.orm.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

@FunctionalInterface
public interface FieldGetter<IN, OUT> extends Serializable {
    @Nullable
    OUT get(@NotNull IN in);
}