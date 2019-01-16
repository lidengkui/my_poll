package com.poll.dao.service.impl; 

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.poll.entity.ext.UserEntityExt;
import com.poll.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.poll.dao.mapper.UserMapper;
import com.poll.dao.service.UserService;
import com.poll.entity.UserEntity;


@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    @Autowired
    protected RedisService redisService;

    @Override
    public void clearCache(Long userId) {
        redisService.clearCacheDay1("poll:user:" + userId);
    }

    @Override
    @Cacheable(value = "day1", key = "'poll:user:' + #userId")
    public UserEntityExt getByIdCache(Long userId) {
        return baseMapper.selectUserExtById(userId);
    }

}