package com.poll.service.module;

import com.poll.common.MsgCode;
import com.poll.common.exception.ApiBizException;
import com.poll.dao.mapper.StaffMapper;
import com.poll.entity.ext.Page;
import com.poll.entity.ext.StaffEntityExt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffModuleService {
    protected Logger log = LogManager.getLogger();
    @Autowired
    private StaffMapper staffMapper;

    public List<StaffEntityExt> staffList(Long companyIdSg, Long mobile, String name, Long groupId,Integer current,Integer size) throws  Exception{
        List<StaffEntityExt> staffEntityExt = null;
        try{
            Page<StaffEntityExt> page = new Page<>();
            page.setCurrent(current);
            page.setSize(size);
            staffEntityExt = staffMapper.staffList(companyIdSg,mobile,name,groupId,current,size);
        }catch (Exception e){
            e.printStackTrace();
            log.info("员工列表查询失败",e.getMessage());
            throw new ApiBizException(MsgCode.C00000040.code,"员工列表查询失败");
        }
        return staffEntityExt;
    }
    public Long staffListCount(Long companyIdSg, Long mobile, String name, Long groupId) throws Exception{
        Long totalCount = 0L;
        try{
            totalCount = staffMapper.staffListCountS(companyIdSg,mobile,name,groupId);
        }catch (Exception e){
            e.printStackTrace();
            log.info("员工列表统计失败",e.getMessage());
            throw new ApiBizException(MsgCode.C00000040.code,"员工列表统计失败");
        }
        return totalCount;
    }
}
