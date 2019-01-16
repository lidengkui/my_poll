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
@TableName("poll_prd_snp")
public class PollPrdSnpEntity implements Serializable {

	@TableId(type = IdType.AUTO)
	private Long idPps;
	private Long pollIdPps;
	private Long companyIdPps;
	private Long userIdPps;
	private String prdCodePps;
	private String prdNamePps;
	private String prdNameExtPps;
	private String prdUnitPps;
	private Integer prdOrderFieldPps;
	private Integer purcsNumPps;
	private Date createTimePps;
}