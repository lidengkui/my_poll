package com.poll.dao.service.impl; 

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.poll.dao.mapper.PollSgSnpMapper;
import com.poll.dao.service.PollSgSnpService;
import com.poll.entity.PollSgSnpEntity;


@Service("pollSgSnpService")
public class PollSgSnpServiceImpl extends ServiceImpl<PollSgSnpMapper, PollSgSnpEntity> implements PollSgSnpService {

}