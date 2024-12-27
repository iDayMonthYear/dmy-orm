package cn.com.idmy.orm.core;

import cn.com.idmy.base.annotation.Table.IdType;
import org.apache.ibatis.type.TypeHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Map;

public record TableInfo(
        @NotNull Class<?> entityClass,
        @NotNull String name,
        @NotNull TableId id,
        @NotNull String comment,
        @NotNull TableColumn[] columns,
        @NotNull Map<String, TableColumn> columnMap) {

    public record TableId(
            @NotNull Field field,
            @NotNull String name,
            @NotNull IdType idType,
            @NotNull String value,
            boolean before,
            @NotNull String comment) {
    }

    public record TableColumn(
            @NotNull Field field,
            @NotNull String name,
            boolean large,
            @NotNull String comment,
            @Nullable
            TypeHandler<?> typeHandler) {
    }
}