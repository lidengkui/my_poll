package com.poll.dao.service;

import com.baomidou.mybatisplus.service.IService;
import com.poll.entity.ThirdAccessEntity;
import com.poll.entity.ext.ThirdAccessEntityExt;


public interface ThirdAccessService extends IService<ThirdAccessEntity> {

    ThirdAccessEntity selectByIdCache(String id);

    ThirdAccessEntityExt selectWithAuthInfo(String id);
}
