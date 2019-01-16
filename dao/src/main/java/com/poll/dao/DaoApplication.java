package com.poll.dao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = {"com.poll"})
public class DaoApplication {
    public static void main(String[] args) {
        System.setProperty("localServerIp","");
        SpringApplication.run(DaoApplication.class, args);
    }
}
