package org.springframework.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented  //会被javadoc处理
public @interface Repository {
    String value() default "";
}
