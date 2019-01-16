package com.poll.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.poll.entity.StaffGpEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository("groupsMapper")
public interface GroupsMapper extends BaseMapper<StaffGpEntity> {

}
