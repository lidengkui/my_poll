package com.poll.dao.service.impl; 

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.poll.entity.ext.Page;
import com.poll.entity.ext.ProductEntityExt;
import org.springframework.stereotype.Service;
import com.poll.dao.mapper.ProductMapper;
import com.poll.dao.service.ProductService;
import com.poll.entity.ProductEntity;

import java.util.List;


@Service("productService")
public class ProductServiceImpl extends ServiceImpl<ProductMapper, ProductEntity> implements ProductService {


    @Override
    public List<ProductEntityExt> selectListByCdt(Byte status, String orderClause, Page<ProductEntityExt> page) {
        return baseMapper.selectListByCdt(status, orderClause, page);
    }
}