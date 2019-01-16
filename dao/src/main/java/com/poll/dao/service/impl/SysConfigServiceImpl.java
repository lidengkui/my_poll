package com.poll.dao.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.poll.dao.mapper.SysConfigMapper;
import com.poll.dao.service.SysConfigService;
import com.poll.entity.SysConfigEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service("sysConfigService")
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfigEntity> implements SysConfigService {

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Override
    @Cacheable(value = "day10", key = "'poll:sysConfig:' + #configKey")
    public SysConfigEntity selectByIdCache(String configKey) {

        if (configKey == null) {
            return null;
        }
        return sysConfigMapper.selectById(configKey);
    }
}
