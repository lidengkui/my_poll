package com.poll.dao.service;

import com.baomidou.mybatisplus.service.IService;
import com.poll.entity.ThirdRoleEntity;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public interface ThirdRoleService extends IService<ThirdRoleEntity> {

    ThirdRoleEntity selectByIdCache(Serializable id);
}
