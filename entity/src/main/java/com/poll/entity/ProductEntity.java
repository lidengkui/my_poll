package com.poll.entity; 

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("product")
public class ProductEntity implements Serializable {

	@TableId(type = IdType.INPUT)
	private String code;
	private String typeCode;
	private String brandCode;
	private String name;
	private String nameExt;
	private String unit;
	private BigDecimal price;
	private Integer orderField;
	private String invoiceType;
	private String invoiceContent;
	private Byte status;
	private String remark;
	private Date createTime;
	private Date updateTime;
}