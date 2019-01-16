package com.poll.dao.mapper; 

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.poll.dao.ext.StaffGpMapperDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.poll.entity.StaffGpEntity;

import java.util.List;


@Mapper
@Repository("staffGpMapper")
public interface StaffGpMapper extends BaseMapper<StaffGpEntity> {

    //更新组成员数量-增加
    StaffGpMapperDto addGpMemberNumGpMemberNum(@Param("companyIdSg")Long companyIdSg,
                                               @Param("groupId")Long groupId,
                                               @Param("successNum")Integer successNum
                                                );

    //更新组成员数量-减少
    Integer decreaseGpMemberNum(@Param("companyIdSg")Long companyIdSg,
                                @Param("groupId")Long groupId,
                                @Param("successNum")Integer successNum);

}