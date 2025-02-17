package cn.com.idmy.orm.core;

import cn.com.idmy.base.FieldGetter;
import cn.com.idmy.base.model.Pair;
import cn.com.idmy.base.util.SqlUtil;
import cn.com.idmy.orm.core.SqlNode.SqlSet;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


@Slf4j
@Accessors(fluent = true, chain = false)
public class Update<T, ID> extends Where<T, ID, Update<T, ID>> {
    protected OrmDao<T, ID> dao;
    protected boolean force;

    protected Update(@NotNull OrmDao<T, ID> dao) {
        super(dao.entityType());
        this.dao = dao;
    }

    @NotNull
    public static <T, ID> Update<T, ID> of(@NotNull OrmDao<T, ID> dao) {
        return new Update<>(dao);
    }

    @NotNull
    public Update<T, ID> set(@NotNull FieldGetter<T, ?> field, @Nullable Object val) {
        return addNode(new SqlSet(entityType, field, val));
    }

    @NotNull
    public Update<T, ID> set(@NotNull FieldGetter<T, ?> field, @Nullable SqlOpExpr expr) {
        return addNode(new SqlSet(entityType, field, expr));
    }

    public void force() {
        force = true;
    }

    public boolean update(@NonNull ID id) {
        eq(id);
        return SqlUtil.toBoolean(dao.update(this));
    }

    public boolean update() {
        return SqlUtil.toBoolean(dao.update(this));
    }

    @NotNull
    @Override
    public Pair<String, List<Object>> sql() {
        return new UpdateSqlGenerator(this).gen();
    }
}
