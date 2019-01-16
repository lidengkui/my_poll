package com.poll.service.business;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.poll.ability.dto.RespMessage;
import com.poll.common.Constants;
import com.poll.common.ConstantsOfParamName;
import com.poll.common.MsgCode;
import com.poll.common.exception.ApiBizException;
import com.poll.common.util.CheckUtil;
import com.poll.common.util.DateUtil;
import com.poll.common.util.RegularUtil;
import com.poll.entity.StaffEntity;
import com.poll.entity.ext.StaffEntityExt;
import com.poll.service.module.GroupsModuleService;
import com.poll.service.module.StaffModuleService;
import com.poll.service.util.TransferUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class StaffService_V1 {
    protected Logger log = LogManager.getLogger();
    @Autowired
    private StaffModuleService staffModuleService;
    @Autowired
    private GroupsModuleService groupsModuleService;
    /**
     * 员工列表
     * @param reqJo
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public RespMessage staffList(JSONObject reqJo, HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject ret = new JSONObject();
        JSONArray groupsJar = new JSONArray();
        Long companyIdSg= TransferUtil.parseUserInfoFromJson(reqJo).getCompanyId();
        Long mobile = CheckUtil.checkParamSimpleFromTo(ConstantsOfParamName.MOBILE, ConstantsOfParamName.MOBILE_ALIAS, Long.class, reqJo, null, true, null, null, null, RegularUtil.phoneNoReg);
        String name = CheckUtil.checkParamSimpleFromTo(ConstantsOfParamName.NAME, ConstantsOfParamName.NAME_ALIAS, String.class, reqJo, null, true, null, null, null, null);
       // Long groupName = CheckUtil.checkParamSimpleFromTo(ConstantsOfParamName.STAFFGPNAME, ConstantsOfParamName.STAFFGP_ALIAS, Long.class, reqJo, null, true, null, null, null, null);
        Long groupId= reqJo.getLong("sgId");
        Integer current = CheckUtil.checkParamSimpleFromTo(ConstantsOfParamName.CURRENT, ConstantsOfParamName.CURRENT_ALIAS, Integer.class, reqJo, null, false, "1", "1", null, RegularUtil.pureNumReg);
        Integer size = CheckUtil.checkParamSimpleFromTo(ConstantsOfParamName.SIZE, ConstantsOfParamName.SIZE_ALIAS, Integer.class, reqJo, null, false, "10", "1", "100", RegularUtil.pureNumReg);

        //列表
        List<StaffEntityExt> staffEntityExtList=  staffModuleService.staffList(companyIdSg,mobile,name,groupId,(current - 1) * size ,size);
        if (null!=staffEntityExtList){
            for (StaffEntityExt staffEntityExt:staffEntityExtList) {
                JSONObject obj = new JSONObject();
                obj.put("uIdS",StringUtils.isEmpty(staffEntityExt.getId()) ? Constants.STR_BLANK : staffEntityExt.getId());
                obj.put("codeS",StringUtils.isEmpty(staffEntityExt.getCode()) ? Constants.STR_BLANK : staffEntityExt.getCode());
                obj.put("creatTime",StringUtils.isEmpty(staffEntityExt.getCreateTime()) ? Constants.STR_BLANK :
                        DateUtil.convertDate2Str( staffEntityExt.getCreateTime(),DateUtil.FORMATE_YYYY_MM_DD_HH_MM_SS_MINUS));
                obj.put("name",StringUtils.isEmpty(staffEntityExt.getName()) ? Constants.STR_BLANK : staffEntityExt.getName());
                obj.put("mobile",StringUtils.isEmpty(staffEntityExt.getMobile()) ? Constants.STR_BLANK : staffEntityExt.getMobile());
                obj.put("memo",StringUtils.isEmpty(staffEntityExt.getRemark()) ? Constants.STR_BLANK : staffEntityExt.getRemark());
                if (null != staffEntityExt.getGroup()){
                    if (!StringUtils.isEmpty(staffEntityExt.getGroup().getNameSg())){
                        obj.put("groupName",StringUtils.isEmpty(staffEntityExt.getGroup().getNameSg()) ? Constants.STR_BLANK : staffEntityExt.getGroup().getNameSg());
                    }
                }else {
                    obj.put("groupName",Constants.STR_BLANK );
                }

                groupsJar.add(obj);
            }
        }
        //统计
        Long totalCount = staffModuleService.staffListCount(companyIdSg,mobile,name,groupId);
        ret.put("staffList",groupsJar);
        ret.put("totalCount",totalCount);
        return  RespMessage.genSuccessWithData(ret);
    }

    /**
     * 员工修改
     * @param reqJo
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public RespMessage updateStaffMember(JSONObject reqJo, HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject ret = new JSONObject();
        Long mobile = CheckUtil.checkParamSimpleFromTo(ConstantsOfParamName.MOBILE, ConstantsOfParamName.MOBILE_ALIAS, Long.class, reqJo, null, false, null, null, null, RegularUtil.phoneNoReg);
        String name = CheckUtil.checkParamSimpleFromTo(ConstantsOfParamName.NAME, "员工姓名", String.class, reqJo, null, false, null, 1,20 ,null);
        Long uId = CheckUtil.checkParamSimpleFromTo(ConstantsOfParamName.UID, ConstantsOfParamName.UID_ALIAS, Long.class, reqJo, null, false, null, null, null, null);

        Long companyIdSg= TransferUtil.parseUserInfoFromJson(reqJo).getCompanyId();
        String memo = CheckUtil.checkParamSimpleFromTo(ConstantsOfParamName.MEMO, ConstantsOfParamName.MEMO_ALIAS, String.class, reqJo, null, true, null, null, null, null);
        groupsModuleService.updateGroupMember(uId,companyIdSg,mobile,name,memo);
        return RespMessage.genSuccess();
    }

    /**
     * 分组管理成员调整
     * @param reqJo
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public RespMessage staffMemberAdjust(JSONObject reqJo, HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject ret = new JSONObject();
        JSONArray uIdS = reqJo.getJSONArray("uIdS");
        Long companyIdSg= TransferUtil.parseUserInfoFromJson(reqJo).getCompanyId();
        Long groupId = CheckUtil.checkParamSimpleFromTo(ConstantsOfParamName.GROUPId, ConstantsOfParamName.GROUPId_ALIAS, Long.class, reqJo, null, false, null, null, null, null);

        List<Long> groupIds = new ArrayList<>();
        if (StringUtils.isEmpty(uIdS)){
            throw new ApiBizException(MsgCode.C00000040.code,"请选中需要转移的员工");
        }

        for (Object guId:uIdS){
            Long groupUserId = Long.valueOf(guId.toString()) ;
            groupIds.add(groupUserId);
        }
        //批量修改
        groupsModuleService .groupMemberAdjust(groupIds,companyIdSg,groupId);

        return RespMessage.genSuccess();
    }

    /**
     * 删除组成员
     * @param reqJo
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public RespMessage deletstaff(JSONObject reqJo, HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject ret = new JSONObject();
        JSONArray uIdS = reqJo.getJSONArray("uIdS");
        if (StringUtils.isEmpty(uIdS)){
            throw new ApiBizException(MsgCode.C00000040.code,"请选中需要删除的员工");
        }
        List<Long> uIdSList = new ArrayList<>();
        for (Object guId:uIdS){
            Long groupUserId = Long.valueOf(guId.toString()) ;
            uIdSList.add(groupUserId);
        }
        Long companyIdSg= TransferUtil.parseUserInfoFromJson(reqJo).getCompanyId();
        //Long groupId = CheckUtil.checkParamSimpleFromTo(ConstantsOfParamName.GROUPNAME, ConstantsOfParamName.GROUPNAME_ALIAS, Long.class, reqJo, null, false, null, null, null, null);

        //批量删除
        groupsModuleService.deletgroupMember(uIdSList,companyIdSg);
        return RespMessage.genSuccess();
    }
}
