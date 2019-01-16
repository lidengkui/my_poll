package com.poll.common.dto;

import lombok.*;

import java.util.Date;

@Data
@ToString
@AllArgsConstructor
public class ResourceCode {

    private String code;            //资源编码
    private long limitMax;			//最大值
    private Date start;			    //开始时间
    private Date expire;		    //真实结束时间
    private Date expireDelay;	    //结束延时时间
}
