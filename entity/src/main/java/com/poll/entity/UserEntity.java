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
@TableName("user")
public class UserEntity implements Serializable {

    private static final long serialVersionUID = 2051928448130944256L;
    @TableId(type = IdType.AUTO)
	private Long id;
	private Long companyId;
	private String userName;
	private Long mobile;
	private String email;
	private String password;
	private String salt;
	private String tokenFlag;
	private Byte status;
	private String remark;
	private Date createTime;
	private Date updateTime;
}