package com.poll.dao.service;

import com.poll.entity.ext.ProductStatisticsEntity;

import java.util.List;

public interface ProductStatisticsService {
    List<ProductStatisticsEntity> listCollectByPollId(long pollId);
}
