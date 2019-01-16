package com.poll.dao.service.impl;

import com.poll.dao.mapper.ProductStatisticsMapper;
import com.poll.dao.service.ProductStatisticsService;
import com.poll.entity.ext.ProductStatisticsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("productStatisticsService")
public class ProductStatisticsServiceImpl implements ProductStatisticsService {
    @Autowired
    private ProductStatisticsMapper productStatisticsMapper;

    @Override
    public List<ProductStatisticsEntity> listCollectByPollId(long pollId) {
        return productStatisticsMapper.listCollectByPollId(pollId);
    }
}
