package cn.com.idmy.orm.core;

import cn.com.idmy.base.annotation.Table.Id.IdType;
import jakarta.annotation.Nullable;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.Field;
import java.util.Map;

public record TableInfo(
        Class<?> entityClass,
        String name,
        TableId id,
        String comment,
        TableColumn[] columns,
        Map<String, TableColumn> columnMap) {

    public record TableId(
            Field field,
            String name,
            IdType idType,
            String value,
            boolean before,
            String comment) {
    }

    public record TableColumn(
            Field field,
            String name,
            boolean large,
            String comment,
            @Nullable
            TypeHandler<?> typeHandler) {
    }
}