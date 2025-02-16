package cn.com.idmy.orm;

import cn.com.idmy.base.annotation.Table;
import cn.com.idmy.base.annotation.Table.IdType;
import cn.com.idmy.orm.core.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.TypeHandler;
import org.dromara.hutool.core.text.StrUtil;
import org.jetbrains.annotations.NotNull;

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
        LOWER("小写"),
        UPPER("大写"),
        LOWER_UNDERLINE("小写下划线"),
        UPPER_UNDERLINE("大写下划线");
        private final String name;
    }

    @Setter
    private boolean enableIEnumValue;

    @Setter
    @NotNull
    private Table.IdType defaultIdType = IdType.AUTO;
    @NotNull
    private NameStrategy tableNameStrategy = NameStrategy.DEFAULT;
    @NotNull
    private NameStrategy columnNameStrategy = NameStrategy.DEFAULT;

    public void defaultBatchSize(int size) {
        if (size > 10) {
            SqlProvider.DEFAULT_BATCH_SIZE = size;
        }
    }

    @NotNull
    private String nameStrategy(@NotNull String name, @NotNull NameStrategy strategy) {
        return switch (strategy) {
            case DEFAULT -> name;
            case LOWER -> name.toLowerCase();
            case UPPER -> name.toUpperCase();
            case LOWER_UNDERLINE -> StrUtil.toUnderlineCase(name).toUpperCase();
            case UPPER_UNDERLINE -> StrUtil.toUnderlineCase(name).toLowerCase();
        };
    }

    @NotNull
    public String toTableName(@NotNull String name) {
        return nameStrategy(name, tableNameStrategy);
    }

    @NotNull
    public String toColumnName(@NotNull String name) {
        return nameStrategy(name, columnNameStrategy);
    }

    public static <T, R> void register(@NotNull Class<T> entityType, @NotNull FieldGetter<T, R> col, @NotNull TypeHandler<?> handler) {
        Tables.bindTypeHandler(entityType, col, handler);
    }

    public static void register(@NotNull CrudInterceptor interceptor) {
        CrudInterceptors.addInterceptor(interceptor);
    }
}