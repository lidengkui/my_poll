package com.poll.dao.service;

import com.baomidou.mybatisplus.service.IService;
import com.poll.entity.ThirdInterfaceEntity;


public interface ThirdInterfaceService extends IService<ThirdInterfaceEntity> {

    ThirdInterfaceEntity selectByServiceMethodNameCache(String serviceName, String methodName);
}
