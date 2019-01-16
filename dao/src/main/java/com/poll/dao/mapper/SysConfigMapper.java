package com.poll.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.poll.entity.SysConfigEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository("sysConfigMapper")
public interface SysConfigMapper extends BaseMapper<SysConfigEntity> {
}
