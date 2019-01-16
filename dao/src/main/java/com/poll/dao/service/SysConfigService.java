package com.poll.dao.service;

import com.baomidou.mybatisplus.service.IService;
import com.poll.entity.SysConfigEntity;


public interface SysConfigService extends IService<SysConfigEntity> {

    /**
     * 查询配置
     * @param configKey
     * @return
     */
    SysConfigEntity selectByIdCache(String configKey);

}
