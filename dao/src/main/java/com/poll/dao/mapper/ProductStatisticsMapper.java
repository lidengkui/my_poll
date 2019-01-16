package com.poll.dao.mapper;

import com.poll.entity.ext.ProductStatisticsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository("productCollectMapper")
public interface ProductStatisticsMapper {
    List<ProductStatisticsEntity> listCollectByPollId(@Param("pollId") long pollId);
}