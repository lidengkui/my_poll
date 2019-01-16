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
@TableName("third_role")
public class ThirdRoleEntity implements Serializable {

	private static final long serialVersionUID = -2348845746777231541L;

	@TableId(type = IdType.INPUT)
	private String codeTr;
	private String nameTr;
	private Byte authTypeTr;
	private String authStrTr;
	private Byte statusTr;
	private Date startTimeTr;
	private Date endTimeTr;
	private String remarkTr;
	private Date createTimeTr;
	private Date updateTimeTr;

}