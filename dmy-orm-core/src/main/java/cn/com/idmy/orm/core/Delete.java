package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.base.util.SqlUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;


@Slf4j
@Getter
@Accessors(fluent = true, chain = false)
public class Delete<T> extends Where<T, Delete<T>> {
    protected OrmDao<T, ?> dao;
    protected boolean force;

    protected Delete(@NotNull OrmDao<T, ?> dao) {
        super(dao.entityType());
        this.dao = dao;
    }

    @NotNull
    public static <T, ID> Delete<T> of(@NotNull OrmDao<T, ID> dao) {
        return new Delete<>(dao);
    }

    public void force() {
        force = true;
    }

    public boolean delete(@NonNull Serializable id) {
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