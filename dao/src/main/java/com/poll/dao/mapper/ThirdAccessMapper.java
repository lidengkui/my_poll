package com.poll.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.poll.entity.ThirdAccessEntity;
import com.poll.entity.ext.ThirdAccessEntityExt;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository("thirdAccessMapper")
public interface ThirdAccessMapper extends BaseMapper<ThirdAccessEntity> {

    ThirdAccessEntityExt selectWithAuthInfo(String code);
}
