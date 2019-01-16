package com.poll.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
@TableName("staff")
public class StaffEntity implements Serializable {
    private static final long serialVersionUID = -561934011537697253L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long companyId;
    private Long userId;
    private Long sgId;
    private String name;
    private Long mobile;
    private String code;
    private String remark;
    private Date createTime;
    private Date updateTime;

}