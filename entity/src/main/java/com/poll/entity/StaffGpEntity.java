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
@TableName("staff_gp")
public class StaffGpEntity implements Serializable {

	@TableId(type = IdType.AUTO)
	private Long idSg;
	private Long companyIdSg;
	private Long userIdSg;
	private String nameSg;
	private Integer memberNumSg;
	private Byte typeSg;
	private String remarkSg;
	private Date createTimeSg;
	private Date updateTimeSg;
}