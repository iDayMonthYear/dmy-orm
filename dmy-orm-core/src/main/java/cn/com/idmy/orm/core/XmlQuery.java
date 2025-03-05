package cn.com.idmy.orm.core;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Slf4j
@Accessors(fluent = false, chain = true)
public class XmlQuery<T> extends Query<T> {
    @Nullable
    private transient XmlQueryGenerator generator;

    protected XmlQuery(@NotNull Class<T> entityType, boolean nullable) {
        super(entityType, nullable);
        force = true;
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
     * &lt;if test="x.cond != null"&gt;
     *     ${x.cond}
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
     * &lt;if test="x.groupBy != null"&gt;
     *     GROUP BY ${x.groupBy}
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
     * 获取查询参数列表，可在MyBatis XML中使用
     * <pre>
     * &lt;if test="x.cond != null"&gt;
     *     AND column = #{x.params[0]}
     * &lt;/if&gt;
     * </pre>
     *
     * @return 参数列表
     */
    @NotNull
    public List<Object> getParams() {
        return generator().params;
    }

    @Override
    @NotNull
    public XmlQuery<T> addNode(@NotNull SqlNode node) {
        super.addNode(node);
        this.generator = null;
        return this;
    }
}