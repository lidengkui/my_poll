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
import com.poll.entity.StaffGpEntity;
import com.poll.entity.UserEntity;
import com.poll.service.module.GroupsModuleService;
import com.poll.service.util.TransferUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class GroupsManageService_V1 {


    protected Logger log = LogManager.getLogger();
    @Autowired
    private GroupsModuleService groupsModuleService;

    /**
     * 分组管理-组列表
     *
     * @param reqJo
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public RespMessage groupsList(JSONObject reqJo, HttpServletRequest request, HttpServletResponse response) throws Exception {

        //参数校验
        Integer current = CheckUtil.checkParamSimpleFromTo(ConstantsOfParamName.CURRENT, ConstantsOfParamName.CURRENT_ALIAS, Integer.class, reqJo, null, false, "1", "1", null, RegularUtil.pureNumReg);
        Integer size = CheckUtil.checkParamSimpleFromTo(ConstantsOfParamName.SIZE, ConstantsOfParamName.SIZE_ALIAS, Integer.class, reqJo, null, false, "10", "1", "100", RegularUtil.pureNumReg);

        JSONArray groupsJar = new JSONArray();
        Page<StaffGpEntity> page = groupsModuleService.groupsList(TransferUtil.parseUserInfoFromJson(reqJo).getCompanyId(), current, size);
        for (StaffGpEntity groupsEntity : page.getRecords()) {
            JSONObject obj = new JSONObject();
            obj.put("groupName", StringUtils.isEmpty(groupsEntity.getNameSg()) ? Constants.STR_BLANK : groupsEntity.getNameSg());
            obj.put("groupNum", groupsEntity.getMemberNumSg());
            obj.put("createDate", DateUtil.convertDate2Str(groupsEntity.getCreateTimeSg(), DateUtil.FORMATE_YYYY_MM_DD_HH_MM_SS_MINUS));
            obj.put("sgId", groupsEntity.getIdSg());
            obj.put("groupType", groupsEntity.getTypeSg());
            groupsJar.add(obj);
        }

        JSONObject ret = new JSONObject();
        ret.put("groupsList", groupsJar);
        ret.put("totalCount", page.getTotal());
        return RespMessage.genSuccessWithData(ret);
    }

    /**
     * 创建组
     *
     * @param reqJo
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public RespMessage creatGroup(JSONObject reqJo, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String groupName = CheckUtil.checkParamSimpleFromTo("groupName", "分组名称", String.class, reqJo, null, false, null, 1, 20, null);

        UserEntity user = TransferUtil.parseUserInfoFromJson(reqJo);

        StaffGpEntity groupsEntity = groupsModuleService.creatGroup(user.getCompanyId(), user.getId(), groupName);

        JSONObject ret = new JSONObject();
        ret.put("groupId", groupsEntity.getIdSg());
        return RespMessage.genSuccessWithData(ret);
    }

    /**
     * 批量删除组
     *
     * @param reqJo
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public RespMessage deletGroup(JSONObject reqJo, HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONArray sgIds = reqJo.getJSONArray("sgIds");
        List<Long> groupIds = new ArrayList<>();
        if (StringUtils.isEmpty(sgIds)) {
            throw new ApiBizException(MsgCode.C00000040.code, "请选择要删除的组");
        }
        for (Object sgId : sgIds) {
            Long groupId = Long.valueOf(sgId.toString());
            groupIds.add(groupId);
        }
        Long companyIdSg = TransferUtil.parseUserInfoFromJson(reqJo).getCompanyId();
        groupsModuleService.deletGroup(companyIdSg, groupIds);
        return RespMessage.genSuccess();
    }

    public RespMessage updateGroup(JSONObject reqJo, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Long groupId = CheckUtil.checkParamSimpleFromTo("sgId", "组标识", Long.class, reqJo, null, false, null, "1", null, null);
        String newGroupName = CheckUtil.checkParamSimpleFromTo("newGroupName", "新组名称", String.class, reqJo, null, false, null, 1, 20, null);

        groupsModuleService.updateGroup(TransferUtil.parseUserInfoFromJson(reqJo).getCompanyId(), groupId, newGroupName);
        return RespMessage.genSuccess();
    }

}
