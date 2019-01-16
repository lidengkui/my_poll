package com.poll.dao.service;

import com.baomidou.mybatisplus.service.IService;
import com.poll.entity.PollSgStfPrdSnpEntity;

import java.util.List;

public interface PollSgStfPrdSnpService extends IService<PollSgStfPrdSnpEntity> {
    List<PollSgStfPrdSnpEntity> listForPagination(long pollId, long offset, int size);
}