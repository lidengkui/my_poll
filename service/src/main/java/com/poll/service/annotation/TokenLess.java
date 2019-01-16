package com.poll.service.annotation;

import java.lang.annotation.*;

/**
 * @description 不校验登录token  方法加此标记，则该方法不进行token校验
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface TokenLess {

    //是否尝试取header中的token并转化为user信息，为空或转化失败时，不报错
    boolean tryParseUser() default false;


}
