
package cn.com.idmy.orm.annotation;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.lang.annotation.*;

/**
 * 数据库表中的列信息注解。
 *
 * @author 开源海哥
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Column {
    /**
     * 字段名称。
     */
    String value() default "";

    /**
     * 是否忽略该字段，可能只是业务字段，而非数据库对应字段。
     */
    boolean ignore() default false;

    /**
     * insert 的时候默认值，这个值会直接被拼接到 sql 而不通过参数设置。
     */
    String onInsertValue() default "";

    /**
     * update 的时候自动赋值，这个值会直接被拼接到 sql 而不通过参数设置。
     */
    String onUpdateValue() default "";

    /**
     * 是否是大字段，大字段 APT 不会生成到 DEFAULT_COLUMNS 里。
     */
    boolean isLarge() default false;

    /**
     * <p>是否是逻辑删除字段，一张表中只能存在 1 一个逻辑删除字段。
     *
     * <p>逻辑删除的字段，被删除时，会设置为 1，正常状态为 0，可以通过 FlexGlobalConfig 配置来修改 1 和 0 为其他值。
     */
    boolean isLogicDelete() default false;

    /**
     * <p>是否为乐观锁字段。
     *
     * <p>若是乐观锁字段的话，数据更新的时候会去检测当前版本号，若更新成功的话会设置当前版本号 +1
     * 只能用于数值的字段。
     */
    boolean version() default false;

    /**
     * 是否是租户 ID。
     */
    boolean tenantId() default false;

    /**
     * 配置的 jdbcType。
     */
    JdbcType jdbcType() default JdbcType.UNDEFINED;

    /**
     * 自定义 TypeHandler。
     */
    Class<? extends TypeHandler> typeHandler() default UnknownTypeHandler.class;

    String mask() default "";

    String[] alias() default "";
}
