package cn.com.idmy.orm.core;

import cn.com.idmy.base.FieldGetter;
import cn.com.idmy.base.config.DefaultConfig;
import cn.com.idmy.base.model.Page;
import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.SqlNode.*;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.bean.BeanUtil;
import org.dromara.hutool.core.reflect.FieldUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.core.util.ObjUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

import static cn.com.idmy.orm.core.Tables.getColumnName;
import static cn.com.idmy.orm.core.Tables.getIdField;
import static cn.com.idmy.orm.core.Tables.getIdName;

@Slf4j
@Accessors(fluent = true, chain = false)
public class Query<T, ID> extends Where<T, ID, Query<T, ID>> {
    @Getter
    @Nullable
    protected Integer offset;
    @Getter
    @Nullable
    protected Integer limit;
    protected boolean hasParam;
    protected boolean force;
    boolean hasSelectColumn;
    protected OrmDao<T, ID> dao;

    protected Query(@NotNull Class<T> entityType, boolean nullable) {
        super(entityType);
        this.nullable = nullable;
    }

    protected Query(@NotNull OrmDao<T, ID> dao, boolean nullable) {
        this(dao.entityType(), nullable);
        this.dao = dao;
    }

    @NotNull
    public static <T, ID> Query<T, ID> of(@NotNull OrmDao<T, ID> dao, boolean nullable) {
        return new Query<>(dao, nullable);
    }

    @NotNull
    public static <T, ID> Query<T, ID> of(@NotNull OrmDao<T, ID> dao) {
        return new Query<>(dao, true);
    }

    public void force() {
        force = true;
    }

    @NotNull
    public Query<T, ID> limit(int limit) {
        this.limit = limit;
        return crud;
    }

    @NotNull
    public Query<T, ID> offset(int offset) {
        this.limit = offset;
        return crud;
    }

    @NotNull
    public Query<T, ID> distinct() {
        hasSelectColumn = true;
        return addNode(new SqlDistinct());
    }

    @NotNull
    public Query<T, ID> distinct(@NotNull FieldGetter<T, ?> field) {
        hasSelectColumn = true;
        return addNode(new SqlDistinct(getColumnName(entityType, field)));
    }

    protected void clearSelectColumns() {
        nodes = nodes.stream().filter(node -> node.type != Type.SELECT_COLUMN && node.type != Type.DISTINCT).collect(Collectors.toList());
    }

    @NotNull
    public Query<T, ID> select(@NotNull SqlFnExpr<T> expr) {
        hasSelectColumn = true;
        return addNode(new SelectSqlColumn(expr));
    }

    @NotNull
    public Query<T, ID> select(@NotNull SqlFnExpr<T> expr, @NotNull FieldGetter<T, ?> alias) {
        hasSelectColumn = true;
        return addNode(new SelectSqlColumn(expr, getColumnName(entityType, alias)));
    }

    @SafeVarargs
    @NotNull
    public final Query<T, ID> select(@NotNull FieldGetter<T, ?>... fields) {
        if (ArrayUtil.isNotEmpty(fields)) {
            hasSelectColumn = true;
            for (var field : fields) {
                addNode(new SelectSqlColumn(getColumnName(entityType, field)));
            }
        }
        return this;
    }

    @NotNull
    public Query<T, ID> groupBy(@NotNull FieldGetter<T, ?> field) {
        return addNode(new SqlGroupBy(getColumnName(entityType, field)));
    }

    @NotNull
    @SafeVarargs
    public final Query<T, ID> groupBy(@NotNull FieldGetter<T, ?>... fields) {
        for (var field : fields) {
            addNode(new SqlGroupBy(getColumnName(entityType, field)));
        }
        return this;
    }

    @NotNull
    public Query<T, ID> orderBy(@NotNull FieldGetter<T, ?> field) {
        return addNode(new SqlOrderBy(getColumnName(entityType, field), false));
    }

    @NotNull
    public Query<T, ID> orderBy(@NotNull FieldGetter<T, ?> field, boolean desc) {
        return addNode(new SqlOrderBy(getColumnName(entityType, field), desc));
    }

    @NotNull
    public Query<T, ID> orderBy(@NotNull FieldGetter<T, ?> field1, boolean desc1, @NotNull FieldGetter<T, ?> field2, boolean desc2) {
        return addNode(new SqlOrderBy(getColumnName(entityType, field1), desc1)).addNode(new SqlOrderBy(getColumnName(entityType, field2), desc2));
    }

    @NotNull
    public Query<T, ID> orderBy(@NotNull FieldGetter<T, ?> field1, boolean desc1, @NotNull FieldGetter<T, ?> field2, boolean desc2, @NotNull FieldGetter<T, ?> field3, boolean desc3) {
        return addNode(new SqlOrderBy(getColumnName(entityType, field1), desc1)).addNode(new SqlOrderBy(getColumnName(entityType, field2), desc2)).addNode(new SqlOrderBy(getColumnName(entityType, field3), desc3));
    }

    @NotNull
    public Query<T, ID> orderBy(@Nullable String[] orders) {
        if (ArrayUtil.isNotEmpty(orders)) {
            if (orders.length % 2 != 0) {
                throw new OrmException("排序列名不成对，必须为：['name', 'asc', 'gender', 'desc']");
            }
            for (int i = 0; i < orders.length; i = i + 2) {
                var fieldName = orders[i];
                if (StrUtil.isBlank(fieldName)) {
                    throw new OrmException("排序字段名不能为空");
                }
                var columnName = getColumnName(entityType, fieldName);
                if (StrUtil.isBlank(columnName)) {
                    throw new OrmException("排序字段名不存在");
                }
                var order = orders[i + 1];
                var desc = StrUtil.equalsIgnoreCase(order, "desc");
                addNode(new SqlOrderBy(columnName, desc));
            }
        }
        return this;
    }

    @NotNull
    public Query<T, ID> param(@Nullable Object param) {
        if (!hasParam && param != null) {
            hasParam = true;
            var createdAtName = DefaultConfig.createdAtName;
            var cats = FieldUtil.getFieldValue(param, createdAtName + "s");
            if (cats instanceof Object[] ats && ArrayUtil.isNotEmpty(ats) && ats.length == 2) {
                var createdAt = getColumnName(entityType, createdAtName);
                if (createdAt != null) {
                    addNode(new SqlCond(createdAt, Op.BETWEEN, ats));
                }
            }
            var updatedAtName = DefaultConfig.updatedAtName;
            var uats = FieldUtil.getFieldValue(param, updatedAtName + "s");
            if (uats instanceof Object[] ats && ArrayUtil.isNotEmpty(ats) && ats.length == 2) {
                var createdAt = getColumnName(entityType, updatedAtName);
                if (createdAt != null) {
                    addNode(new SqlCond(createdAt, Op.BETWEEN, ats));
                }
            }

            var idField = getIdField(entityType);
            var idVal = FieldUtil.getFieldValue(param, idField);
            if (idVal != null) {
                addNode(new SqlCond(getIdName(entityType), Op.EQ, idVal));
            } else {
                var ids = FieldUtil.getFieldValue(param, idField.getName() + "s");
                if (ObjUtil.isNotEmpty(ids)) {
                    addNode(new SqlCond(getIdName(entityType), Op.IN, ids));
                }
            }
        }
        return this;
    }

    public <E> List<E> list(Class<E> type) {
        if (type == entityType) {
            throw new OrmException("不能查询为当前实体");
        } else {
            var ls = dao.list(this);
            return BeanUtil.copyToList(ls, type);
        }
    }

    public <E> E get(Class<E> type) {
        if (type == entityType) {
            throw new OrmException("不能查询为当前实体");
        } else {
            T t = dao.get(this);
            return BeanUtil.copyProperties(t, type);
        }
    }

    public <E> Page<E> page(Page<?> in, Class<E> type) {
        if (type == entityType) {
            throw new OrmException("不能查询为当前实体");
        } else {
            Page<T> page = dao.page(in, this);
            if (page.isEmpty()) {
                return Page.empty();
            } else {
                return page.convert(t -> BeanUtil.copyProperties(t, type));
            }
        }
    }

    @NotNull
    @Override
    public Pair<String, List<Object>> sql() {
        return new QuerySqlGenerator(this).gen();
    }
}
