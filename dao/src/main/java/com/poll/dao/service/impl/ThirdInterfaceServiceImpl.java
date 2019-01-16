package com.poll.dao.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.poll.dao.mapper.ThirdInterfaceMapper;
import com.poll.dao.service.ThirdInterfaceService;
import com.poll.entity.ThirdInterfaceEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service("thirdInterfaceService")
public class ThirdInterfaceServiceImpl extends ServiceImpl<ThirdInterfaceMapper, ThirdInterfaceEntity> implements ThirdInterfaceService {

    @Override
    @Cacheable(value = "day10", key = "'poll:thirdInterface:' + #serviceName + '_' + #methodName")
    public ThirdInterfaceEntity selectByServiceMethodNameCache(String serviceName, String methodName) {

        Wrapper<ThirdInterfaceEntity> wrapper = new EntityWrapper<ThirdInterfaceEntity>();
        wrapper.eq("service_name_ti", serviceName);
        wrapper.eq("method_name_ti", methodName);
        return selectOne(wrapper);
    }
}
