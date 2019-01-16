package com.poll.dao.mapper; 

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.poll.entity.ext.PollEntityExt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.poll.entity.PollEntity;

import java.util.List;


@Mapper
@Repository("pollMapper")
public interface PollMapper extends BaseMapper<PollEntity> {

    /**
     * 查询调研表详情
     * @param id
     * @param companyId
     * @return
     */
    PollEntityExt queryDetailById(@Param("id")long id, @Param("companyId")long companyId);

}