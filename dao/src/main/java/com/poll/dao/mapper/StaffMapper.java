package com.poll.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.poll.entity.StaffEntity;
import com.poll.entity.ext.Page;
import com.poll.entity.ext.StaffEntityExt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository("staffMapper")
public interface StaffMapper extends BaseMapper<StaffEntity> {

    /**
     * 通过员工id查找组-员工信息
     * @param uId
     * @param companyIdSg
     * @return
     */
    StaffEntityExt findGroupByUid(@Param("uId")Long uId, @Param("companyIdSg")Long companyIdSg);

    Integer updateBatchGroup(@Param("uIds")List<Long> uIds,
                             @Param("companyIdSg")Long companyIdSg,
                             @Param("groupId")Long groupId,
                             @Param("sgIdNe")Long sgIdNe);

    Integer deletBatchGroupNum(@Param("uIds")List<Long> uIds, @Param("companyIdSg")Long companyIdSg);

    List<StaffEntityExt> staffList(@Param("companyIdSg")Long companyIdSg, @Param("mobile")Long mobile, @Param("name")String name ,
                                   @Param("groupId")Long groupId, @Param("current")Integer current, @Param("size") Integer size);


   Long staffListCountS(@Param("companyIdSg")Long companyIdSg,@Param("mobile")Long mobile,@Param("name")String name ,
                                   @Param("groupId")Long groupId);

    List<StaffEntityExt> staffGroupList(@Param("companyIdSg")Long companyIdSg, @Param("uIds")List<Long> uIds );

    /**
     * 查询列表
     * @param companyId
     * @param sgIdList
     * @param orderClause
     * @param offset
     * @param size
     * @return
     */
    List<StaffEntity> selectListByCdt(@Param("companyId")Long companyId,
                                 @Param("sgIdList")List<Long> sgIdList,
                                 @Param("orderClause")String orderClause,
                                 @Param("offset")Integer offset,
                                 @Param("size")Integer size);

}