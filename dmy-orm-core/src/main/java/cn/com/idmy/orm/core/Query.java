package cn.com.idmy.orm.core;

import cn.com.idmy.base.config.DefaultConfig;
import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.SqlNode.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.lang.Console;
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
public class Query<T> extends Where<T, Query<T>> {
    boolean hasSelectColumn;
    @Getter
    @Setter
    @Nullable
    protected Integer offset;

    @Getter
    @Setter
    @Nullable
    protected Integer limit;
    protected boolean hasParam;
    protected boolean force;

    protected Query(@NotNull Class<T> entityType) {
        super(entityType);
    }

    @NotNull
    public static <T, ID> Query<T> of(@NotNull MybatisDao<T, ID> dao) {
        Console.log(dao.entityType());
        return new Query<>(dao.entityType());
    }

    public void force() {
        force = true;
    }

    @NotNull
    public Query<T> distinct() {
        hasSelectColumn = true;
        return addNode(new SqlDistinct());
    }

    @NotNull
    public Query<T> distinct(@NotNull FieldGetter<T, ?> field) {
        hasSelectColumn = true;
        return addNode(new SqlDistinct(getColumnName(entityType, field)));
    }

    protected void clearSelectColumns() {
        nodes = nodes.stream().filter(node -> node.type != Type.SELECT_COLUMN && node.type != Type.DISTINCT).collect(Collectors.toList());
    }

    @NotNull
    public Query<T> select(@NotNull SqlFnExpr<T> expr) {
        hasSelectColumn = true;
        return addNode(new SelectSqlColumn(expr));
    }

    @NotNull
    public Query<T> select(@NotNull SqlFnExpr<T> expr, @NotNull FieldGetter<T, ?> alias) {
        hasSelectColumn = true;
        return addNode(new SelectSqlColumn(expr, getColumnName(entityType, alias)));
    }

    @SafeVarargs
    @NotNull
    public final Query<T> select(@NotNull FieldGetter<T, ?>... fields) {
        if (ArrayUtil.isNotEmpty(fields)) {
            hasSelectColumn = true;
            for (var field : fields) {
                addNode(new SelectSqlColumn(getColumnName(entityType, field)));
            }
        }
        return this;
    }

    @NotNull
    public Query<T> groupBy(@NotNull FieldGetter<T, ?> field) {
        return addNode(new SqlGroupBy(getColumnName(entityType, field)));
    }

    @NotNull
    @SafeVarargs
    public final Query<T> groupBy(@NotNull FieldGetter<T, ?>... fields) {
        for (var field : fields) {
            addNode(new SqlGroupBy(getColumnName(entityType, field)));
        }
        return this;
    }

    @NotNull
    public Query<T> orderBy(@NotNull FieldGetter<T, ?> field) {
        return addNode(new SqlOrderBy(getColumnName(entityType, field), false));
    }

    @NotNull
    public Query<T> orderBy(@NotNull FieldGetter<T, ?> field, boolean desc) {
        return addNode(new SqlOrderBy(getColumnName(entityType, field), desc));
    }

    @NotNull
    public Query<T> orderBy(@NotNull FieldGetter<T, ?> field1, boolean desc1, @NotNull FieldGetter<T, ?> field2, boolean desc2) {
        return addNode(new SqlOrderBy(getColumnName(entityType, field1), desc1)).addNode(new SqlOrderBy(getColumnName(entityType, field2), desc2));
    }

    @NotNull
    public Query<T> orderBy(@NotNull FieldGetter<T, ?> field1, boolean desc1, @NotNull FieldGetter<T, ?> field2, boolean desc2, @NotNull FieldGetter<T, ?> field3, boolean desc3) {
        return addNode(new SqlOrderBy(getColumnName(entityType, field1), desc1)).addNode(new SqlOrderBy(getColumnName(entityType, field2), desc2)).addNode(new SqlOrderBy(getColumnName(entityType, field3), desc3));
    }

    @NotNull
    public Query<T> orderBy(@Nullable String[] orders) {
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
    public Query<T> param(@Nullable Object param) {
        if (!hasParam && param != null) {
            hasParam = true;
            var cats = FieldUtil.getFieldValue(param, DefaultConfig.createdAt + "s");
            if (cats instanceof Object[] ats && ArrayUtil.isNotEmpty(ats) && ats.length == 2) {
                var createdAt = getColumnName(entityType, DefaultConfig.createdAt);
                if (createdAt != null) {
                    addNode(new SqlCond(createdAt, Op.BETWEEN, ats));
                }
            }
            var uats = FieldUtil.getFieldValue(param, DefaultConfig.updatedAt + "s");
            if (uats instanceof Object[] ats && ArrayUtil.isNotEmpty(ats) && ats.length == 2) {
                var createdAt = getColumnName(entityType, DefaultConfig.createdAt);
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

    @NotNull
    @Override
    public Pair<String, List<Object>> sql() {
        return new QuerySqlGenerator(this).gen();
    }
}
