package com.poll.dao.service.impl; 

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.poll.entity.ext.PollEntityExt;
import org.springframework.stereotype.Service;
import com.poll.dao.mapper.PollMapper;
import com.poll.dao.service.PollService;
import com.poll.entity.PollEntity;

import java.util.List;


@Service("pollService")
public class PollServiceImpl extends ServiceImpl<PollMapper, PollEntity> implements PollService {



    @Override
    public PollEntityExt queryDetailById(long pollId, long companyId) {
        return baseMapper.queryDetailById(pollId, companyId);
    }
}