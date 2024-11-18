
package cn.com.idmy.orm.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface TableId {
    Type value() default Type.AUTO;

    boolean before() default true;

    String comment() default "";

    enum Type {
        AUTO,
        SEQUENCE,
        GENERATOR,
        NONE,
    }
}