package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.core.SqlNode.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.text.StrUtil;

import java.util.List;
import java.util.stream.Collectors;

import static cn.com.idmy.orm.core.Tables.getColumnName;

@Slf4j
@Accessors(fluent = true, chain = false)
public class Selects<T> extends Where<T, Selects<T>> {
    boolean hasSelectColumn;
    @Getter
    @Setter
    protected Integer offset;
    @Getter
    @Setter
    protected Integer limit;

    protected Selects(Class<T> entityClass) {
        super(entityClass);
    }

    public static <T, ID> Selects<T> of(MybatisDao<T, ID> dao) {
        return new Selects<>(dao.entityClass());
    }

    public Selects<T> distinct() {
        hasSelectColumn = true;
        return addNode(new SqlDistinct());
    }

    public Selects<T> distinct(FieldGetter<T, ?> field) {
        hasSelectColumn = true;
        return addNode(new SqlDistinct(getColumnName(entityClass, field)));
    }

    void clearSelectColumns() {
        nodes = nodes.stream().filter(node -> node.type != SqlNodeType.SELECT_COLUMN && node.type != SqlNodeType.DISTINCT).collect(Collectors.toList());
    }

    public Selects<T> select(SqlFnExpr<T> expr) {
        hasSelectColumn = true;
        return addNode(new SqlSelectColumn(expr));
    }

    public Selects<T> select(SqlFnExpr<T> expr, FieldGetter<T, ?> alias) {
        hasSelectColumn = true;
        return addNode(new SqlSelectColumn(expr, getColumnName(entityClass, alias)));
    }

    @SafeVarargs
    public final Selects<T> select(FieldGetter<T, ?>... fields) {
        if (ArrayUtil.isNotEmpty(fields)) {
            hasSelectColumn = true;
            for (FieldGetter<T, ?> field : fields) {
                addNode(new SqlSelectColumn(getColumnName(entityClass, field)));
            }
        }
        return this;
    }

    public Selects<T> groupBy(FieldGetter<T, ?> field) {
        return addNode(new SqlGroupBy(getColumnName(entityClass, field)));
    }

    @SafeVarargs
    public final Selects<T> groupBy(FieldGetter<T, ?> field, FieldGetter<T, ?>... fields) {
        addNode(new SqlGroupBy(getColumnName(entityClass, field)));
        for (FieldGetter<T, ?> f : fields) {
            addNode(new SqlGroupBy(getColumnName(entityClass, f)));
        }
        return this;
    }

    public Selects<T> orderBy(FieldGetter<T, ?> field) {
        return addNode(new SqlOrderBy(getColumnName(entityClass, field), false));
    }

    public Selects<T> orderBy(FieldGetter<T, ?> field, boolean desc) {
        return addNode(new SqlOrderBy(getColumnName(entityClass, field), desc));
    }

    public Selects<T> orderBy(FieldGetter<T, ?> field1, boolean desc1, FieldGetter<T, ?> field2, boolean desc2) {
        return addNode(new SqlOrderBy(getColumnName(entityClass, field1), desc1))
                .addNode(new SqlOrderBy(getColumnName(entityClass, field2), desc2));
    }

    public Selects<T> orderBy(FieldGetter<T, ?> field1, boolean desc1, FieldGetter<T, ?> field2, boolean desc2, FieldGetter<T, ?> field3, boolean desc3) {
        return addNode(new SqlOrderBy(getColumnName(entityClass, field1), desc1))
                .addNode(new SqlOrderBy(getColumnName(entityClass, field2), desc2))
                .addNode(new SqlOrderBy(getColumnName(entityClass, field3), desc3));
    }

    public Selects<T> orderBy(String[] orders) {
        Assert.isTrue(orders.length % 2 == 0, "排序列名不成对，必须为：['name', 'asc', 'gender', 'desc']");
        for (int i = 0; i < orders.length; i = i + 2) {
            var col = getColumnName(entityClass, orders[i]);
            var desc = StrUtil.equalsIgnoreCase(orders[i + 1], "desc");
            addNode(new SqlOrderBy(col, desc));
        }
        return this;
    }

    @Override
    public Pair<String, List<Object>> sql() {
        return new SelectSqlGenerator(this).generate();
    }
}
