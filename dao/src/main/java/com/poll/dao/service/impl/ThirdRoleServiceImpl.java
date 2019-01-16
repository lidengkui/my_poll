package com.poll.dao.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.poll.dao.mapper.ThirdRoleMapper;
import com.poll.dao.service.ThirdRoleService;
import com.poll.entity.ThirdRoleEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;


@Service("thirdRoleService")
public class ThirdRoleServiceImpl extends ServiceImpl<ThirdRoleMapper, ThirdRoleEntity> implements ThirdRoleService {

    @Override
    @Cacheable(value = "day10", key = "'poll:thirdRole:' + #id")
    public ThirdRoleEntity selectByIdCache(Serializable id) {
        return this.selectById(id);
    }
}
