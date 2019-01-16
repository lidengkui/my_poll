package com.poll.dao.service.impl; 

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.poll.dao.mapper.StaffGpMapper;
import com.poll.dao.service.StaffGpService;
import com.poll.entity.StaffGpEntity;

@Service("staffGpService")
public class StaffGpServiceImpl extends ServiceImpl<StaffGpMapper, StaffGpEntity> implements StaffGpService {

}