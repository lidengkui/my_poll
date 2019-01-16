package com.poll.service.module.event;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.poll.service.module.event.EventProperties.PREFIX;

/**
 * @author lidengkui
 * @className EventProperties
 * @description
 * @date 2019/1/15 0015
 */

@ConfigurationProperties(PREFIX)
@Data
public class EventProperties {
    public static final String PREFIX = "event";
    private boolean enable = true;
}
