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
@TableName("third_role_interface_rlt")
public class ThirdRoleInterfaceRltEntity implements Serializable {


	private static final long serialVersionUID = -4171715969543617068L;

	@TableId(type = IdType.AUTO)
	private Long idTrir;
	private String roleCodeTrir;
	private String interfaceCodeTrir;
	private String versionTrir;
	private Byte statusTrir;
	private Date startTimeTrir;
	private Date endTimeTrir;
	private String remarkTrir;
	private Date createTimeTrir;
	private Date updateTimeTrir;

}