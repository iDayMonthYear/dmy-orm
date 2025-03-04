package cn.com.idmy.orm.core;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@Slf4j
@Accessors(fluent = false, chain = true)
public class XmlQuery<T, ID> extends Query<T, ID> {
    @Nullable
    private transient XmlQueryGenerator generator;

    protected XmlQuery(@NotNull OrmDao<T, ID> dao, boolean nullable) {
        super(dao, nullable);
        force = true;
    }

    @NotNull
    public static <T, ID> XmlQuery<T, ID> of(@NotNull OrmDao<T, ID> dao, boolean nullable) {
        return new XmlQuery<>(dao, nullable);
    }

    @NotNull
    private XmlQueryGenerator generator() {
        if (generator == null) {
            generator = XmlQueryGenerator.of(this);
        }
        return generator;
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
        return generator().getConditionString();
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
        return generator().getOrderByString();
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
        return generator().getGroupByString();
    }

    /**
     * 重写父类方法，清除查询生成器缓存
     */
    @Override
    @NotNull
    public XmlQuery<T, ID> addNode(@NotNull SqlNode node) {
        super.addNode(node);
        this.generator = null;
        return this;
    }

    public @NotNull List<Object> params() {
        return generator == null ? Collections.emptyList() : generator.params;
    }
} 