package com.poll.entity; 

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("poll")
public class PollEntity implements Serializable {

	@TableId(type = IdType.AUTO)
	private Long id;
	private Long companyId;
	private Long userId;
	private String code;
	private String name;
	private String sgNames;
	private Integer sgMemberNum;
	private Date createTime;
	private Date updateTime;
}