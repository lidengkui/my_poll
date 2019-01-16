package com.poll.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableCaching
public class RedisApplication {
    public static void main(String[] args) {
        System.setProperty("localServerIp","");
        SpringApplication.run(RedisApplication.class, args);
    }
}
