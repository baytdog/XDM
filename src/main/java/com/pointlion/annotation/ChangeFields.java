package com.pointlion.annotation;

import java.lang.annotation.*;

/**
 * @Author: king
 * @date: 2022/12/1 16:44
 * @description:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ChangeFields {
    String value() ;
    String desc() default "";

}
