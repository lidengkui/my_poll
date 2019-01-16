package com.poll.dao.mapper; 

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.poll.entity.ext.Page;
import com.poll.entity.ext.ProductEntityExt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.poll.entity.ProductEntity;

import java.util.List;


@Mapper
@Repository("productMapper")
public interface ProductMapper extends BaseMapper<ProductEntity> {


    List<ProductEntityExt> selectListByCdt(@Param("status")Byte status, @Param("orderClause")String orderClause, @Param("page")Page<ProductEntityExt> page);
}