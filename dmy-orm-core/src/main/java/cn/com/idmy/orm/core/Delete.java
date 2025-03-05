package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
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

    @NotNull
    @Override
    public Pair<String, List<Object>> sql() {
        return new DeleteSqlGenerator(this).generate();
    }
}