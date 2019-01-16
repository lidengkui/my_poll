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
@TableName("poll_sg_stf_prd_snp")
public class PollSgStfPrdSnpEntity implements Serializable {

	@TableId(type = IdType.AUTO)
	private Long idPssps;
	private Long pssIdPssps;
	private Long pollIdPssps;
	private Long companyIdPssps;
	private Long userIdPssps;
	private Long sgIdPssps;
	private Long stfIdPssps;
	private String stfNamePssps;
	private Long stfMobilePssps;
	private String prdCodePssps;
	private String prdNamePssps;
	private String prdNameExtPssps;
	private String prdUnitPssps;
	private Integer prdOrderFieldPssps;
	private Integer purcsNumPssps;
	private Date createTimePssps;
}