package com.poll.dao.service;

import com.baomidou.mybatisplus.service.IService;
import com.poll.entity.StaffEntity;




public interface StaffService extends IService<StaffEntity> {


    public StaffEntity findGroupByUid(Long groupUid);
}
