package com.poll.ability;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = { "com.poll"})
public class AbilityApplication {
    public static void main(String[] args) {
//        try {
//            InetAddress address = InetAddress.getLocalHost();
//            String ip = address.getHostAddress();
//            System.setProperty("localServerIp",ip+"-");
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//            System.setProperty("localServerIp","");
//        }
        System.setProperty("localServerIp","");
        SpringApplication.run(AbilityApplication.class, args);
    }
}
