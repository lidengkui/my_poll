package com.poll.service.module;

import com.poll.common.Constants;
import com.poll.dao.service.ProductService;
import com.poll.entity.ext.ProductEntityExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProductModuleService {

    @Autowired
    private ProductService productService;

    /**
     * 可用产品列表
     * @return
     */
    @Cacheable(value = "minute5", key = "'poll:product:availableList'")
    public List<ProductEntityExt> getAvailableList() {
        return productService.selectListByCdt(Constants.BYTE1, "order_field_pt, order_field_pb, order_field", null);
    }
}
