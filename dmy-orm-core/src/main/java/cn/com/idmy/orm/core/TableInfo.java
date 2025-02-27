package cn.com.idmy.orm.core;

import cn.com.idmy.base.annotation.IdType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.lang.reflect.Field;
import java.util.Map;

public record TableInfo(@NotNull Class<?> entityType,
                        @Nullable String schema,
                        @NotNull String name,
                        @NotNull TableId id,
                        @Nullable String title,
                        @NotNull TableColumn[] columns,
                        @NotNull Map<String, TableColumn> columnMap) {

    public record TableId(@NotNull Field field,
                          @NotNull String name,
                          @NotNull IdType idType,
                          @Nullable String value,
                          @Nullable String key,
                          boolean before,
                          @Nullable String title) {
    }

    public record TableColumn(@NotNull Field field, @NotNull String name, @Nullable String title) {
    }
}