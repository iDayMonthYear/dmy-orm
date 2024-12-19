package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.base.model.Param;
import cn.com.idmy.orm.core.SqlNode.*;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.text.StrUtil;

import java.util.List;
import java.util.stream.Collectors;

import static cn.com.idmy.base.constant.DefaultConsts.CREATED_AT;
import static cn.com.idmy.base.constant.DefaultConsts.UPDATED_AT;
import static cn.com.idmy.orm.core.Tables.getColumnName;
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

    protected Query(Class<T> entityClass) {
        super(entityClass);
    }

    public static <T, ID> Query<T> of(MybatisDao<T, ID> dao) {
        return new Query<>(dao.entityClass());
    }

    public Query<T> distinct() {
        hasSelectColumn = true;
        return addNode(new SqlDistinct());
    }

    public Query<T> distinct(FieldGetter<T, ?> field) {
        hasSelectColumn = true;
        return addNode(new SqlDistinct(getColumnName(entityClass, field)));
    }

    void clearSelectColumns() {
        nodes = nodes.stream().filter(node -> node.type != SqlNodeType.SELECT_COLUMN && node.type != SqlNodeType.DISTINCT).collect(Collectors.toList());
    }

    public Query<T> select(SqlFnExpr<T> expr) {
        hasSelectColumn = true;
        return addNode(new SqlSelectColumn(expr));
    }

    public Query<T> select(SqlFnExpr<T> expr, FieldGetter<T, ?> alias) {
        hasSelectColumn = true;
        return addNode(new SqlSelectColumn(expr, getColumnName(entityClass, alias)));
    }

    @SafeVarargs
    public final Query<T> select(FieldGetter<T, ?>... fields) {
        if (ArrayUtil.isNotEmpty(fields)) {
            hasSelectColumn = true;
            for (FieldGetter<T, ?> field : fields) {
                addNode(new SqlSelectColumn(getColumnName(entityClass, field)));
            }
        }
        return this;
    }

    public Query<T> groupBy(FieldGetter<T, ?> field) {
        return addNode(new SqlGroupBy(getColumnName(entityClass, field)));
    }

    @SafeVarargs
    public final Query<T> groupBy(FieldGetter<T, ?>... fields) {
        for (FieldGetter<T, ?> field : fields) {
            addNode(new SqlGroupBy(getColumnName(entityClass, field)));
        }
        return this;
    }

    public Query<T> orderBy(FieldGetter<T, ?> field) {
        return addNode(new SqlOrderBy(getColumnName(entityClass, field), false));
    }

    public Query<T> orderBy(FieldGetter<T, ?> field, boolean desc) {
        return addNode(new SqlOrderBy(getColumnName(entityClass, field), desc));
    }

    public Query<T> orderBy(FieldGetter<T, ?> field1, boolean desc1, FieldGetter<T, ?> field2, boolean desc2) {
        return addNode(new SqlOrderBy(getColumnName(entityClass, field1), desc1))
                .addNode(new SqlOrderBy(getColumnName(entityClass, field2), desc2));
    }

    public Query<T> orderBy(FieldGetter<T, ?> field1, boolean desc1, FieldGetter<T, ?> field2, boolean desc2, FieldGetter<T, ?> field3, boolean desc3) {
        return addNode(new SqlOrderBy(getColumnName(entityClass, field1), desc1))
                .addNode(new SqlOrderBy(getColumnName(entityClass, field2), desc2))
                .addNode(new SqlOrderBy(getColumnName(entityClass, field3), desc3));
    }

    public Query<T> orderBy(@Nullable String[] orders) {
        if (ArrayUtil.isNotEmpty(orders)) {
            Assert.isTrue(orders.length % 2 == 0, "排序列名不成对，必须为：['name', 'asc', 'gender', 'desc']");
            for (int i = 0; i < orders.length; i = i + 2) {
                var col = getColumnName(entityClass, orders[i]);
                var desc = StrUtil.equalsIgnoreCase(orders[i + 1], "desc");
                addNode(new SqlOrderBy(col, desc));
            }
        }
        return this;
    }

    public Query<T> param(@Nullable Param<?> param) {
        if (param != null) {
            var id = param.getId();
            if (id != null) {
                addNode(new SqlCond(getIdName(entityClass), Op.EQ, id));
            } else {
                var ids = param.getIds();
                if (CollUtil.isNotEmpty(ids)) {
                    addNode(new SqlCond(getIdName(entityClass), Op.IN, ids));
                } else {
                    var notIds = param.getIds();
                    if (CollUtil.isNotEmpty(notIds)) {
                        addNode(new SqlCond(getIdName(entityClass), Op.NOT_IN, notIds));
                    }
                }
            }
            var createdAts = param.getCreatedAts();
            if (ArrayUtil.isNotEmpty(createdAts) && createdAts.length == 2) {
                var createdAt = getColumnName(entityClass, CREATED_AT);
                if (createdAt != null) {
                    addNode(new SqlCond(createdAt, Op.BETWEEN, createdAts));
                }
            }
            var updatedAts = param.getUpdatedAts();
            if (ArrayUtil.isNotEmpty(updatedAts) && updatedAts.length == 2) {
                var updatedAt = getColumnName(entityClass, UPDATED_AT);
                if (updatedAt != null) {
                    addNode(new SqlCond(updatedAt, Op.BETWEEN, createdAts));
                }
            }
        }
        return this;
    }

    @Override
    public Pair<String, List<Object>> sql() {
        return new QuerySqlGenerator(this).generate();
    }
}
