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
@TableName("sys_config")
public class SysConfigEntity implements Serializable {

	private static final long serialVersionUID = 816318597643553386L;
	@TableId(type = IdType.INPUT)
	private String configKey;
	private String configValue1;
	private String configValue2;
	private String configValue3;
	private String configValue4;
	private String remark;
	private Date createTime;
	private Date updateTime;

}