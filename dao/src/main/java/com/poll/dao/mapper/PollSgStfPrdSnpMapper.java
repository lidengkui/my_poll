package com.poll.dao.mapper; 

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.poll.entity.PollSgStfPrdSnpEntity;

import java.util.List;


@Mapper
@Repository("pollSgStfPrdSnpMapper")
public interface PollSgStfPrdSnpMapper extends BaseMapper<PollSgStfPrdSnpEntity> {
    List<PollSgStfPrdSnpEntity> listForPagination(@Param("pollId") long pollId, @Param("offset") long offset, @Param("size") int size);
}