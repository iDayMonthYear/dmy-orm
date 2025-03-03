package cn.com.idmy.orm.core;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.text.StrUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 专门用于在MyBatis XML中使用的查询对象
 * 可以在XML中通过 ${query.cond}、${query.orderBy}、${query.groupBy} 使用链式查询条件
 */
@Slf4j
@Accessors(fluent = false, chain = true)
public class XmlQuery<T, ID> extends Query<T, ID> {
    /**
     * 创建XmlQuery实例
     *
     * @param dao      ORM DAO对象
     * @param nullable 是否允许空值
     */
    protected XmlQuery(@NotNull OrmDao<T, ID> dao, boolean nullable) {
        super(dao, nullable);
    }

    /**
     * 创建XmlQuery实例
     *
     * @param query 查询对象
     */
    protected XmlQuery(@NotNull Query<T, ID> query) {
        super(query.dao, query.nullable);
        this.nodes = query.nodes;
        this.limit = query.limit;
        this.offset = query.offset;
        this.hasParam = query.hasParam;
        this.hasSelectColumn = query.hasSelectColumn;
        this.hasAggregate = query.hasAggregate;
        this.hasCond = query.hasCond;
        this.force = query.force;
        this.sqlParamsSize = query.sqlParamsSize;
    }

    /**
     * 创建XmlQuery实例
     *
     * @param dao  ORM DAO对象
     * @param <T>  实体类型
     * @param <ID> 主键类型
     * @return XmlQuery实例
     */
    @NotNull
    public static <T, ID> XmlQuery<T, ID> of(@NotNull OrmDao<T, ID> dao) {
        return new XmlQuery<>(dao, true);
    }

    /**
     * 将Query转换为XmlQuery
     *
     * @param query 查询对象
     * @param <T>   实体类型
     * @param <ID>  主键类型
     * @return XmlQuery实例
     */
    @NotNull
    public static <T, ID> XmlQuery<T, ID> of(@NotNull Query<T, ID> query) {
        return new XmlQuery<>(query);
    }

    /**
     * 获取条件字符串，可在MyBatis XML中使用
     * <pre>
     * &lt;if test="xxx.cond != null"&gt;
     *     ${xxx.cond}
     * &lt;/if&gt;
     * </pre>
     *
     * @return 条件字符串
     */
    @Nullable
    public String getCond() {
        String condStr = QueryConditionConverter.toConditionString(this);
        return StrUtil.isNotBlank(condStr) ? condStr : null;
    }

    /**
     * 获取排序字符串，可在MyBatis XML中使用
     * <pre>
     * &lt;if test="xxx.orderBy != null"&gt;
     *     ORDER BY ${xxx.orderBy}
     * &lt;/if&gt;
     * </pre>
     *
     * @return 排序字符串
     */
    @Nullable
    public String getOrderBy() {
        String orderByStr = QueryConditionConverter.toOrderByString(this);
        return StrUtil.isNotBlank(orderByStr) ? orderByStr : null;
    }

    /**
     * 获取分组字符串，可在MyBatis XML中使用
     * <pre>
     * &lt;if test="xxx.groupBy != null"&gt;
     *     GROUP BY ${xxx.groupBy}
     * &lt;/if&gt;
     * </pre>
     *
     * @return 分组字符串
     */
    @Nullable
    public String getGroupBy() {
        String groupByStr = QueryConditionConverter.toGroupByString(this);
        return StrUtil.isNotBlank(groupByStr) ? groupByStr : null;
    }
} 