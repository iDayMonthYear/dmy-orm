package cn.com.idmy.orm.core;

import cn.com.idmy.orm.annotation.Table.Id.IdType;
import jakarta.annotation.Nullable;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.Field;
import java.util.Map;

public record TableInfo(
        Class<?> entityClass,
        String name,
        TableIdInfo id,
        String comment,
        TableColumnInfo[] columns,
        Map<String, TableColumnInfo> columnMap) {

    public record TableIdInfo(
            Field field,
            String name,
            String value,
            IdType idType,
            boolean before,
            String comment) {
    }

    public record TableColumnInfo(
            Field field,
            String name,
            boolean large,
            String comment,
            @Nullable
            TypeHandler<?> typeHandler) {
    }
}