package com.poll.dao.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.poll.dao.mapper.ThirdRoleInterfaceRltMapper;
import com.poll.dao.service.ThirdRoleInterfaceRltService;
import com.poll.entity.ThirdRoleInterfaceRltEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;


@Service("thirdRoleInterfaceRltService")
public class ThirdRoleInterfaceRltServiceImpl extends ServiceImpl<ThirdRoleInterfaceRltMapper, ThirdRoleInterfaceRltEntity> implements ThirdRoleInterfaceRltService {

    @Override
    @Cacheable(value = "day10", key = "'poll:thirdRoleInterfaceRlt:' + #id")
    public ThirdRoleInterfaceRltEntity selectByIdCache(Serializable id) {
        return this.selectById(id);
    }
}
