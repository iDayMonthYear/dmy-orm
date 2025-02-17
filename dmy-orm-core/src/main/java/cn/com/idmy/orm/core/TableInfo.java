package cn.com.idmy.orm.core;

import cn.com.idmy.base.annotation.IdType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Map;

public record TableInfo(@NotNull Class<?> entityType, @NotNull String name, @NotNull TableId id, @Nullable String title, @NotNull TableColumn[] columns, @NotNull Map<String, TableColumn> columnMap) {
    public record TableId(@NotNull Field field, @NotNull String name, @NotNull IdType idType, @NotNull String value, boolean before, @Nullable String title) {
    }
    public record TableColumn(@NotNull Field field, @NotNull String name, @Nullable String title) {
    }
}