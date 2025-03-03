package cn.com.idmy.orm.core;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
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
     * 查询生成器缓存，避免重复生成
     */
    @Nullable
    private transient XmlQueryGenerator queryGenerator;

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
     * 获取查询生成器，如果缓存不存在则创建
     *
     * @return 查询生成器
     */
    @NotNull
    private XmlQueryGenerator getQueryGenerator() {
        if (queryGenerator == null) {
            queryGenerator = XmlQueryGenerator.of(this);
        }
        return queryGenerator;
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
        return getQueryGenerator().getConditionString();
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
        return getQueryGenerator().getOrderByString();
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
        return getQueryGenerator().getGroupByString();
    }

    /**
     * 重写父类方法，清除查询生成器缓存
     */
    @Override
    @NotNull
    public XmlQuery<T, ID> addNode(@NotNull SqlNode node) {
        super.addNode(node);
        this.queryGenerator = null;
        return this;
    }
} 