package com.poll.dao.mapper; 

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.poll.entity.ext.UserEntityExt;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import com.poll.entity.UserEntity;


@Mapper
@Repository("userMapper")
public interface UserMapper extends BaseMapper<UserEntity> {

    UserEntityExt selectUserExtById(Long userId);

}