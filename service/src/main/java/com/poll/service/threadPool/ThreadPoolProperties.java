package com.poll.service.threadPool;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.poll.service.threadPool.ThreadPoolProperties.PREFIX;

@ConfigurationProperties(prefix = PREFIX)
@Data
@Slf4j
public class ThreadPoolProperties {

    public static final String PREFIX = "acooly.threadpool";
    private int threadMin = 1;
    private int threadMax = 50;
    private int threadQueue = 10;

}
