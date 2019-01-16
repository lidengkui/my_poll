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
@TableName("third_access")
public class ThirdAccessEntity implements Serializable {

	private static final long serialVersionUID = 1960145392264658284L;

	@TableId(type = IdType.INPUT)
	private String code;
	private String name;
    private String alias;
    private String roleCode;
    private String partnerCode;
	private String privateKey;
	private String publicKey;
	private String secretKey;
	private String codeAtThird;
	private String aliasAtThird;
	private String partnerCodeAtThird;
	private String privateKeyAtThird;
	private String publicKeyAtThird;
	private String signFlag;
	private String encTransformat;
	private String extConf;
	private String accessServiceObjCode;
	private Byte devType;
	private Byte status;
    private String remark;
    private Date createTime;
    private Date updateTime;

}