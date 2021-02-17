package org.springframework.annotation;

import java.lang.annotation.*;

/**
 * @author ：XXXX
 * @date ：Created in 2021/2/7
 * @description ：
 * @version: 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented  //会被javadoc处理
public @interface Service {
    String value() default "";
}
