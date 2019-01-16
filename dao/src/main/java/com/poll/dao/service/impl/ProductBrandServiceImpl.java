package com.poll.dao.service.impl; 

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.poll.dao.mapper.ProductBrandMapper;
import com.poll.dao.service.ProductBrandService;
import com.poll.entity.ProductBrandEntity;


@Service("productBrandService")
public class ProductBrandServiceImpl extends ServiceImpl<ProductBrandMapper, ProductBrandEntity> implements ProductBrandService {

}