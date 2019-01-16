package com.poll.common.exception;

import lombok.Data;

import java.util.Date;


@Data
public class RollbackRedisDto {

    private String key;
    private int addValue;
    private long maxValue;
    private Date expireAt;
    private String upperTips;
}
