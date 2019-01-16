package com.poll.common.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
public class PtcptExtParamDto {

    private String ptcptParam;
    private String openId;
    private String avatar;
    private String nickName;
    private String channel;
    private String remark;

}
