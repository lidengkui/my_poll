package com.poll.dao.service;

import com.baomidou.mybatisplus.service.IService;
import com.poll.entity.ThirdRoleInterfaceRltEntity;

import java.io.Serializable;


public interface ThirdRoleInterfaceRltService extends IService<ThirdRoleInterfaceRltEntity> {

    ThirdRoleInterfaceRltEntity selectByIdCache(Serializable id);
}
