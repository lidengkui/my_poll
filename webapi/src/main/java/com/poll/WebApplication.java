package com.poll;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class WebApplication {

    public static void main(String[] args) {
        System.setProperty("localServerIp","");
        ConfigurableApplicationContext context = SpringApplication.run(WebApplication.class, args);
//        ApplicationContextUtil.applicationContext = context;
    }
}
