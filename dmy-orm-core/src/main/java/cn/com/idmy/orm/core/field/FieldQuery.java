package cn.com.idmy.orm.core.field;

import cn.com.idmy.orm.core.util.FieldWrapper;
import lombok.Data;

import java.io.Serializable;

/**
 * 查询属性的信息。
 */
@Data
public class FieldQuery implements Serializable {
    private Class<?> entityClass;
    private String fieldName;
    private FieldWrapper fieldWrapper;
    private boolean prevent;
    private QueryBuilder queryBuilder;

    public static class Builder<T> {
        private final FieldQuery fieldQuery;

        public Builder(Class entityClass, String fieldName) {
            this.fieldQuery = new FieldQuery();
            this.fieldQuery.setEntityClass(entityClass);
            this.fieldQuery.setFieldName(fieldName);
            this.fieldQuery.setFieldWrapper(FieldWrapper.of(entityClass, fieldName));
        }

        /**
         * 阻止对嵌套类属性的查询，只对 集合 与 实体类 两种属性类型有效。
         *
         * @return 构建者
         */
        public Builder<T> prevent() {
            this.fieldQuery.setPrevent(true);
            return this;
        }

        /**
         * 设置是否阻止对嵌套类属性的查询，只对 集合 与 实体类 两种属性类型有效。
         *
         * @param prevent 是否阻止对嵌套类属性查询
         * @return 构建者
         */
        public Builder<T> prevent(boolean prevent) {
            this.fieldQuery.setPrevent(prevent);
            return this;
        }

        /**
         * 设置查询这个属性的 {@code QueryWrapper} 对象。
         *
         * @param queryBuilder 查询包装器
         * @return 构建者
         */
        public Builder<T> queryWrapper(QueryBuilder<T> queryBuilder) {
            this.fieldQuery.setQueryBuilder(queryBuilder);
            return this;
        }

        protected FieldQuery build() {
            return this.fieldQuery;
        }

    }
}
