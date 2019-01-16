package com.poll.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = { "com.poll"})
public class ServiceApplication {
    public static void main(String[] args) {
        System.setProperty("localServerIp","");
        SpringApplication.run(ServiceApplication.class, args);
    }
}
