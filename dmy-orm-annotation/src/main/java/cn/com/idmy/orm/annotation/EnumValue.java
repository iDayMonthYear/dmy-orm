
package cn.com.idmy.orm.annotation;

import java.lang.annotation.*;

/**
 * 枚举属性注解。
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface EnumValue {
}
