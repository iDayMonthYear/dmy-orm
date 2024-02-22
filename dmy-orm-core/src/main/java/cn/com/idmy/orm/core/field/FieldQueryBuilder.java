package cn.com.idmy.orm.core.field;

import cn.com.idmy.orm.core.exception.OrmAssert;
import cn.com.idmy.orm.core.util.LambdaGetter;
import cn.com.idmy.orm.core.util.LambdaUtil;

import java.io.Serializable;

/**
 * 属性查询构建者。
 *
 * @param <T> 属性所在实体类类型
 * @author 开源海哥
 * @author 王帅
 */
public class FieldQueryBuilder<T> implements Serializable {

    private FieldQuery.Builder<?> builder;

    /**
     * 为指定属性创建查询。
     *
     * @param fn Lambda 引用
     * @return {@link FieldQuery.Builder} 构建者
     */
    public FieldQuery.Builder<T> field(LambdaGetter<T> fn) {
        return createBuilder(fn);
    }

    /**
     * 为指定嵌套属性创建查询。
     *
     * @param <N> 嵌套属性类型
     * @param fn  Lambda 引用
     * @return {@link FieldQuery.Builder} 构建者
     */
    public <N> FieldQuery.Builder<N> nestedField(LambdaGetter<N> fn) {
        return createBuilder(fn);
    }

    /**
     * <p>创建 {@link FieldQuery.Builder} 用于构建属性类型与 {@code QueryWrapper} 用于查询。
     *
     * <p>该方法主要用作于创建构建者对象，以及泛型的转换。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private <R> FieldQuery.Builder<R> createBuilder(LambdaGetter fn) {
        OrmAssert.notNull(fn, "Field can not be null.");
        Class<?> entityClass = LambdaUtil.getImplClass(fn);
        String fieldName = LambdaUtil.getFieldName(fn);
        builder = new FieldQuery.Builder<>(entityClass, fieldName);
        return (FieldQuery.Builder<R>) builder;
    }

    public FieldQuery build() {
        return builder.build();
    }

}
