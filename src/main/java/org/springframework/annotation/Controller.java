package org.springframework.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)    //作用在类上
@Documented  //会被javadoc处理
public @interface Controller {
    String value() default "";
}
