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
    protected Delete(@NotNull Class<T> entityClass) {
        super(entityClass);
    }

    @NotNull
    public static <T, ID> Delete<T> of(@NotNull MybatisDao<T, ID> dao) {
        return new Delete<>(dao.entityClass());
    }

    @NotNull
    @Override
    public Pair<String, List<Object>> sql() {
        return new DeleteSqlGenerator(this).generate();
    }
}