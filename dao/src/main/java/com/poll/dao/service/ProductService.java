package com.poll.dao.service; 

import com.baomidou.mybatisplus.service.IService;
import com.poll.entity.ProductEntity;
import com.poll.entity.ext.Page;
import com.poll.entity.ext.ProductEntityExt;

import java.util.List;


public interface ProductService extends IService<ProductEntity> {


    List<ProductEntityExt> selectListByCdt(Byte status, String orderClause, Page<ProductEntityExt> page);
}