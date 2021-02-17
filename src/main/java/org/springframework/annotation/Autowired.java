package org.springframework.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)  //定义在属性上面
@Documented  //会被javadoc处理
public @interface Autowired {
//    boolean required() default true;    //是否是必须的
    //自己添加一个属性
    String value() default "";
}
