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
@TableName("product_type")
public class ProductTypeEntity implements Serializable {

	@TableId(type = IdType.INPUT)
	private String codePt;
	private String namePt;
	private Integer orderFieldPt;
	private Byte statusPt;
	private String remarkPt;
	private Date createTimePt;
	private Date updateTimePt;
}