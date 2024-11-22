package cn.com.idmy.orm.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Table {
    String value() default "";

    String schema() default "";

    String comment() default "";

    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @interface Id {
        IdType type() default IdType.AUTO;

        String value() default "";

        String name() default "";

        boolean before() default true;

        String comment() default "";

        enum IdType {
            AUTO,
            GENERATOR,
            SEQUENCE,
            NONE
        }
    }

    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @interface Column {
        String value() default "";

        boolean ignore() default false;

        boolean large() default false;

        boolean logicDelete() default false;

        boolean version() default false;

        boolean tenant() default false;

        String comment() default "";
    }
}