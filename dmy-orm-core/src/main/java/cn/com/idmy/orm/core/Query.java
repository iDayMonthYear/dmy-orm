package cn.com.idmy.orm.core;

import cn.com.idmy.base.FieldGetter;
import cn.com.idmy.base.config.DefaultConfig;
import cn.com.idmy.base.model.Page;
import cn.com.idmy.base.util.Assert;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.SqlNode.*;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.bean.BeanUtil;
import org.dromara.hutool.core.lang.tuple.Pair;
import org.dromara.hutool.core.reflect.FieldUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.core.util.ObjUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

import static cn.com.idmy.orm.core.Tables.getColumnName;
import static cn.com.idmy.orm.core.Tables.getIdField;

@Slf4j
@Accessors(fluent = true, chain = false)
public class Query<T> extends Where<T, Query<T>> {
    @Getter
    protected @Nullable Integer offset;
    @Getter
    protected @Nullable Integer limit;
    protected boolean hasCommon;
    protected boolean hasSelectColumn;
    protected boolean hasAggregate;
    protected OrmDao<T, ?> dao;

    protected Query(@NotNull Class<T> entityType, boolean nullable) {
        super(entityType);
        this.nullable = nullable;
    }

    protected Query(@NotNull OrmDao<T, ?> dao, boolean nullable) {
        this(dao.entityType(), nullable);
        this.dao = dao;
    }

    public @NotNull Query<T> limit(int limit) {
        this.limit = limit;
        return crud;
    }

    public @NotNull Query<T> offset(int offset) {
        this.offset = offset;
        return crud;
    }

    public @NotNull Query<T> one() {
        limit = 1;
        return crud;
    }

    public @NotNull Query<T> distinct() {
        hasSelectColumn = true;
        return addNode(new SqlDistinct());
    }

    public @NotNull Query<T> distinct(@NotNull FieldGetter<T, ?> field) {
        hasSelectColumn = true;
        return addNode(new SqlDistinct(getColumnName(entityType, field)));
    }

    protected void clearSelectColumns() {
        nodes = nodes.stream().filter(node -> node.type != Type.SELECT_COLUMN && node.type != Type.DISTINCT).collect(Collectors.toList());
    }

    public @NotNull Query<T> select(@NotNull SqlFnExpr<T> expr) {
        hasSelectColumn = true;
        hasAggregate = true;
        return addNode(new SelectSqlColumn(expr));
    }

    public @NotNull Query<T> select(@NotNull SqlFnExpr<T> expr, @NotNull FieldGetter<T, ?> alias) {
        hasSelectColumn = true;
        hasAggregate = true;
        return addNode(new SelectSqlColumn(expr, getColumnName(entityType, alias)));
    }

    public final @SafeVarargs @NotNull Query<T> select(@NotNull FieldGetter<T, ?>... fields) {
        if (ArrayUtil.isNotEmpty(fields)) {
            hasSelectColumn = true;
            for (int i = 0, len = fields.length; i < len; i++) {
                addNode(new SelectSqlColumn(getColumnName(entityType, fields[i])));
            }
        }
        return this;
    }

    public @NotNull Query<T> groupBy(@NotNull FieldGetter<T, ?> field) {
        hasAggregate = true;
        return addNode(new SqlGroupBy(getColumnName(entityType, field)));
    }

    public final @SafeVarargs @NotNull Query<T> groupBy(@NotNull FieldGetter<T, ?>... fields) {
        hasAggregate = true;
        for (int i = 0, len = fields.length; i < len; i++) {
            addNode(new SqlGroupBy(getColumnName(entityType, fields[i])));
        }
        return this;
    }

    public @NotNull Query<T> orderBy(@NotNull FieldGetter<T, ?> field) {
        return addNode(new SqlOrderBy(getColumnName(entityType, field), false));
    }

    public @NotNull Query<T> orderBy(@NotNull FieldGetter<T, ?> field, boolean desc) {
        return addNode(new SqlOrderBy(getColumnName(entityType, field), desc));
    }

    public @NotNull Query<T> orderByDesc(@NotNull FieldGetter<T, ?> field) {
        return orderBy(field, true);
    }

    public @NotNull Query<T> orderBy(@NotNull FieldGetter<T, ?> field1, boolean desc1, @NotNull FieldGetter<T, ?> field2, boolean desc2) {
        return addNode(new SqlOrderBy(getColumnName(entityType, field1), desc1)).addNode(new SqlOrderBy(getColumnName(entityType, field2), desc2));
    }

    public @NotNull Query<T> orderBy(@NotNull FieldGetter<T, ?> field1, boolean desc1, @NotNull FieldGetter<T, ?> field2, boolean desc2, @NotNull FieldGetter<T, ?> field3, boolean desc3) {
        return addNode(new SqlOrderBy(getColumnName(entityType, field1), desc1)).addNode(new SqlOrderBy(getColumnName(entityType, field2), desc2)).addNode(new SqlOrderBy(getColumnName(entityType, field3), desc3));
    }

    public @NotNull Query<T> orderBy(@NotNull FieldGetter<T, ?> field1, @NotNull FieldGetter<T, ?> field2) {
        return orderBy(field1, false, field2, false);
    }

    public @NotNull Query<T> orderBy(@NotNull FieldGetter<T, ?> field1, @NotNull FieldGetter<T, ?> field2, @NotNull FieldGetter<T, ?> field3) {
        return orderBy(field1, false, field2, false, field3, false);
    }

    public @NotNull Query<T> orderByDesc(@NotNull FieldGetter<T, ?> field1, @NotNull FieldGetter<T, ?> field2) {
        return orderBy(field1, true, field2, true);
    }

    public @NotNull Query<T> orderByDesc(@NotNull FieldGetter<T, ?> field1, @NotNull FieldGetter<T, ?> field2, @NotNull FieldGetter<T, ?> field3) {
        return orderBy(field1, true, field2, true, field3, true);
    }

    public @NotNull Query<T> orderBy(@Nullable String[] orders) {
        if (ArrayUtil.isNotEmpty(orders)) {
            if (orders.length % 2 != 0) {
                throw new OrmException("排序列名不成对，必须为：['name', 'asc', 'gender', 'desc']");
            }
            for (int i = 0; i < orders.length; i = i + 2) {
                var fieldName = Assert.notBlank(orders[i], "排序字段名不能为空");
                var columnName = Assert.notBlank(getColumnName(entityType, fieldName), "排序字段名不存在");
                var order = orders[i + 1];
                addNode(new SqlOrderBy(columnName, StrUtil.equalsIgnoreCase(order, "desc")));
            }
        }
        return crud;
    }

    public @NotNull Query<T> common(@Nullable Object params) {
        if (!hasCommon && params != null) {
            hasCommon = true;
            var createdAtName = DefaultConfig.createdAtName;
            var cats = FieldUtil.getFieldValue(params, createdAtName + "s");
            if (cats instanceof Object[] ats && ArrayUtil.isNotEmpty(ats) && ats.length == 2) {
                var createdAt = getColumnName(entityType, createdAtName);
                if (createdAt != null) {
                    addNode(new SqlCond(createdAt, Op.BETWEEN, ats));
                }
            }
            var updatedAtName = DefaultConfig.updatedAtName;
            var uats = FieldUtil.getFieldValue(params, updatedAtName + "s");
            if (uats instanceof Object[] ats && ArrayUtil.isNotEmpty(ats) && ats.length == 2) {
                var createdAt = getColumnName(entityType, updatedAtName);
                if (createdAt != null) {
                    addNode(new SqlCond(createdAt, Op.BETWEEN, ats));
                }
            }

            var idField = getIdField(entityType);
            var idVal = FieldUtil.getFieldValue(params, idField);
            if (idVal != null) {
                addNode(new SqlCond(Tables.getIdColumnName(entityType), Op.EQ, idVal));
            } else {
                var ids = FieldUtil.getFieldValue(params, idField.getName() + "s");
                if (ObjUtil.isNotEmpty(ids)) {
                    addNode(new SqlCond(Tables.getIdColumnName(entityType), Op.IN, ids));
                }
            }
        }
        return crud;
    }

    public @NotNull <E> List<E> list(Class<E> type) {
        if (type == entityType) {
            throw new OrmException("不能查询为当前实体");
        } else {
            var ls = dao.list(this);
            return BeanUtil.copyToList(ls, type);
        }
    }

    public @NotNull <E> E get(Class<E> type) {
        if (type == entityType) {
            throw new OrmException("不能查询为当前实体");
        } else {
            T t = dao.get(this);
            return BeanUtil.copyProperties(t, type);
        }
    }

    public @NotNull <E> Page<E> page(Page<?> in, Class<E> type) {
        if (type == entityType) {
            throw new OrmException("不能查询为当前实体");
        } else {
            var page = dao.page(in, this);
            if (page.isEmpty()) {
                return Page.empty();
            } else {
                return page.convert(t -> BeanUtil.copyProperties(t, type));
            }
        }
    }

    @Override
    public @NotNull Pair<String, List<Object>> sql() {
        return new QuerySqlGenerator(this).generate();
    }
}
