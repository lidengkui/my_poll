package com.poll.entity.ext;

import lombok.Data;

import java.io.Serializable;


@Data
public class ProductStatisticsEntity implements Serializable {
    private static final long serialVersionUID = -7908793073971881630L;

    //权益名称
    private String prdtName;
    //权益扩展名
    private String prdtExtName;
    //权益单位
    private String prdtUnit;
    //总数量  (产品数量 *  所属组的人数和)
    private int prdtTotal;
}
