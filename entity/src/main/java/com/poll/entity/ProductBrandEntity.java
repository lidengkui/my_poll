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
@TableName("product_brand")
public class ProductBrandEntity implements Serializable {

	@TableId(type = IdType.INPUT)
	private String codePb;
	private String namePb;
	private Integer orderFieldPb;
	private Byte statusPb;
	private String remarkPb;
	private Date createTimePb;
	private Date updateTimePb;
}