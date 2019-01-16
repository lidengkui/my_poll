package com.poll.dao.service.impl; 

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.poll.dao.mapper.PollPrdSnpMapper;
import com.poll.dao.service.PollPrdSnpService;
import com.poll.entity.PollPrdSnpEntity;


@Service("pollPrdSnpService")
public class PollPrdSnpServiceImpl extends ServiceImpl<PollPrdSnpMapper, PollPrdSnpEntity> implements PollPrdSnpService {

}