package com.poll.dao.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.poll.dao.mapper.PollSgStfPrdSnpMapper;
import com.poll.dao.service.PollSgStfPrdSnpService;
import com.poll.entity.PollSgStfPrdSnpEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("pollSgStfPrdSnpService")
public class PollSgStfPrdSnpServiceImpl extends ServiceImpl<PollSgStfPrdSnpMapper, PollSgStfPrdSnpEntity> implements PollSgStfPrdSnpService {
    @Autowired
    private PollSgStfPrdSnpMapper pollSgStfPrdSnpMapper;

    @Override
    public List<PollSgStfPrdSnpEntity> listForPagination(long pollId, long offset, int size) {
        return pollSgStfPrdSnpMapper.listForPagination(pollId, offset, size);
    }
}