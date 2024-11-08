
package cn.com.idmy.orm.annotation;

import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface TableField {
    String value() default "";

    boolean ignore() default false;

    boolean logicDelete() default false;

    boolean version() default false;

    boolean tenantId() default false;

    Class<? extends TypeHandler> typeHandler() default UnknownTypeHandler.class;

    String comment() default "";
}