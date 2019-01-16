package com.poll.dao.service; 

import com.baomidou.mybatisplus.service.IService;
import com.poll.entity.UserEntity;
import com.poll.entity.ext.UserEntityExt;


public interface UserService extends IService<UserEntity> {

    void clearCache(Long userId);

    UserEntityExt getByIdCache(Long userId);

}