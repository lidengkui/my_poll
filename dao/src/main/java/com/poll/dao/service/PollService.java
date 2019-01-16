package com.poll.dao.service; 

import com.baomidou.mybatisplus.service.IService;
import com.poll.entity.PollEntity;
import com.poll.entity.ext.PollEntityExt;

import java.util.List;


public interface PollService extends IService<PollEntity> {

    /**
     * 查询调研表详情
     * @param pollId
     * @param companyId
     * @return
     */
    PollEntityExt queryDetailById(long pollId, long companyId);

}