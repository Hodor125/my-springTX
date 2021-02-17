package org.springframework.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)  //定义在方法上面
@Documented  //会被javadoc处理
public @interface Transactional {
}
