package cn.com.idmy.orm;

import cn.com.idmy.orm.core.ColumnGetter;
import cn.com.idmy.orm.core.Tables;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.TypeHandler;
import org.dromara.hutool.core.text.StrUtil;

@Getter
@Accessors(fluent = true)
public class OrmConfig {
    @Getter
    private final static OrmConfig config = new OrmConfig();

    @Accessors(fluent = false)
    @Getter
    @RequiredArgsConstructor
    public enum NameStrategy {
        DEFAULT("默认"),
        LOWER_UNDERLINE("小写下划线"),
        UPPER_UNDERLINE("小写下划线");
        private final String name;
    }

    private NameStrategy tableNameStrategy = NameStrategy.DEFAULT;
    private NameStrategy columnNameStrategy = NameStrategy.DEFAULT;

    public String toTableName(String name) {
        return switch (tableNameStrategy) {
            case DEFAULT -> name;
            case LOWER_UNDERLINE -> StrUtil.toUnderlineCase(name).toUpperCase();
            case UPPER_UNDERLINE -> StrUtil.toUnderlineCase(name).toLowerCase();
        };
    }

    public String toColumnName(String name) {
        return switch (columnNameStrategy) {
            case DEFAULT -> name;
            case LOWER_UNDERLINE -> StrUtil.toUnderlineCase(name).toUpperCase();
            case UPPER_UNDERLINE -> StrUtil.toUnderlineCase(name).toLowerCase();
        };
    }

    public static <T, R> void register(Class<T> entityClass, ColumnGetter<T, R> col, TypeHandler<?> handler) {
        Tables.register(entityClass, col, handler);
    }
}