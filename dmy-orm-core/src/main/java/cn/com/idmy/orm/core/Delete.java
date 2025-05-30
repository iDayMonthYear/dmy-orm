package cn.com.idmy.orm.core;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.lang.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;


@Slf4j
@Getter
@Accessors(fluent = true, chain = false)
public class Delete<T> extends Where<T, Delete<T>> {
    protected Delete(@NotNull Class<T> entityType, boolean nullable) {
        super(entityType);
        this.nullable = nullable;
    }

    @Override
    public @NotNull Pair<String, List<Object>> sql() {
        return new DeleteSqlGenerator(this).generate();
    }
}