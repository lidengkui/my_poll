package com.poll.dao.service.impl; 

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.poll.dao.mapper.ProductTypeMapper;
import com.poll.dao.service.ProductTypeService;
import com.poll.entity.ProductTypeEntity;


@Service("productTypeService")
public class ProductTypeServiceImpl extends ServiceImpl<ProductTypeMapper, ProductTypeEntity> implements ProductTypeService {

}