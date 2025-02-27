package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.base.util.SqlUtil;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Slf4j
@Getter
@Accessors(fluent = true, chain = false)
public class Delete<T, ID> extends Where<T, ID, Delete<T, ID>> {
    protected OrmDao<T, ID> dao;
    protected boolean force;

    protected Delete(@NotNull OrmDao<T, ID> dao, boolean nullable) {
        super(dao.entityType());
        this.nullable = nullable;
        this.dao = dao;
    }

    @NotNull
    public static <T, ID> Delete<T, ID> of(@NotNull OrmDao<T, ID> dao, boolean nullable) {
        return new Delete<>(dao, nullable);
    }

    public void force() {
        force = true;
    }

    public boolean delete(@NonNull ID id) {
        eq(id);
        return SqlUtil.toBoolean(dao.delete(this));
    }

    public boolean delete() {
        return SqlUtil.toBoolean(dao.delete(this));
    }

    @NotNull
    @Override
    public Pair<String, List<Object>> sql() {
        return new DeleteSqlGenerator(this).gen();
    }
}