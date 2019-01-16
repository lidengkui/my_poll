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
@TableName("third_interface")
public class ThirdInterfaceEntity implements Serializable {


	private static final long serialVersionUID = 5349469729345170036L;

	@TableId(type = IdType.INPUT)
	private String codeTi;
	private String nameTi;
	private String serviceNameTi;
	private String methodNameTi;
	private Byte statusTi;
	private Date startTimeTi;
	private Date endTimeTi;
    private String remarkTi;
    private Date createTimeTi;
    private Date updateTimeTi;

}