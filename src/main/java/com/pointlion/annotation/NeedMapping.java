package com.pointlion.annotation;

import javax.annotation.Resource;
import java.lang.annotation.*;

/**
 * @Author: king
 * @date: 2022/12/22 14:05
 * @description: 标注需要和字典映射的字段
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NeedMapping {
    String dictName();
}
