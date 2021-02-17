package org.springframework.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)    //作用在类上
@Documented  //会被javadoc处理
public @interface Around {
    String execution() default "";
}
