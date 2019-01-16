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
@TableName("poll_sg_snp")
public class PollSgSnpEntity implements Serializable {

	@TableId(type = IdType.AUTO)
	private Long idPss;
	private Long pollIdPss;
	private Long companyIdPss;
	private Long userIdPss;
	private Long sgIdPss;
	private String sgNamePss;
	private Integer sgMemberNumPss;
	private Date createTimePss;
}