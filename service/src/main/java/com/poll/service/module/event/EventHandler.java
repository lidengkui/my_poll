package com.poll.service.module.event;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author lidengkui
 * @className EventHandler
 * @description
 * @date 2019/1/15 0015
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface EventHandler {
}
