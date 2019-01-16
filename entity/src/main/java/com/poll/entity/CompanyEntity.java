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
@TableName("company")
public class CompanyEntity implements Serializable {

	private static final long serialVersionUID = -6345798554623863700L;
	@TableId(type = IdType.AUTO)
	private Long id;
	private String name;
	private String remark;
	private Date createTime;
	private Date updateTime;
}