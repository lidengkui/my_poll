package com.poll.dao.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.poll.dao.mapper.StaffMapper;
import com.poll.dao.service.StaffService;
import com.poll.entity.StaffEntity;



@Service("staffService")
public class StaffServiceImpl extends ServiceImpl<StaffMapper, StaffEntity> implements StaffService {




    @Override
    public StaffEntity findGroupByUid(Long groupUid) {

        return null;
    }
}
