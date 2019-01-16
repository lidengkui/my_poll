package com.poll.dao.mapper; 

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import com.poll.entity.ProductTypeEntity;

@Mapper
@Repository("productTypeMapper")
public interface ProductTypeMapper extends BaseMapper<ProductTypeEntity> {

}