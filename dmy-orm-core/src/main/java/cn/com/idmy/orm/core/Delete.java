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
public class Delete<T, ID> extends Where<T, ID, Delete<T, ID>> {

    protected Delete(@NotNull OrmDao<T, ID> dao, boolean nullable) {
        super(dao);
        this.nullable = nullable;
    }

    @NotNull
    protected static <T, ID> Delete<T, ID> of(@NotNull OrmDao<T, ID> dao, boolean nullable) {
        return new Delete<>(dao, nullable);
    }

    @NotNull
    @Override
    public Pair<String, List<Object>> sql() {
        return new DeleteSqlGenerator(this).generate();
    }
}