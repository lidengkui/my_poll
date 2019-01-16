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
import com.poll.entity.*;
import com.poll.entity.ext.PollEntityExt;
import com.poll.entity.ext.PollProductExt;
import com.poll.service.module.GroupsModuleService;
import com.poll.service.module.PollModuleService;
import com.poll.service.util.TransferUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PollService_V1 {
    protected Logger log = LogManager.getLogger();
    @Autowired
    private PollModuleService pollModuleService;
    @Autowired
    private GroupsModuleService groupsModuleService;

    public RespMessage pollList(JSONObject reqJo, HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject ret = new JSONObject();
        JSONArray pollJar = new JSONArray();
        Integer current = CheckUtil.checkParamSimpleFromTo(ConstantsOfParamName.CURRENT, ConstantsOfParamName.CURRENT_ALIAS, Integer.class, reqJo, null, false, "1", "1", null, RegularUtil.pureNumReg);
        Integer size = CheckUtil.checkParamSimpleFromTo(ConstantsOfParamName.SIZE, ConstantsOfParamName.SIZE_ALIAS, Integer.class, reqJo, null, false, "10", "1", "100", RegularUtil.pureNumReg);
        String pollName = reqJo.getString("pollName");
        String pollCode = reqJo.getString("pollCode");
        Date pollCreateStartTime = reqJo.getDate("pollCreateStartTime");
        Date pollCreateEndTime =reqJo.getDate("pollCreateEndTime");
        Long companyIdSg=TransferUtil.parseUserInfoFromJson(reqJo).getCompanyId();
        Page<PollEntity> pollList = pollModuleService.pollList(companyIdSg,current,size,pollName,pollCode,pollCreateStartTime,pollCreateEndTime);
        if (null != pollList.getRecords()){
            for (PollEntity pollEntity : pollList.getRecords()) {
                JSONObject obj = new JSONObject();
                obj.put("pollCode",StringUtils.isEmpty(pollEntity.getCode()) ? Constants.STR_BLANK : pollEntity.getCode());
                obj.put("createTime",StringUtils.isEmpty(pollEntity.getCreateTime()) ? Constants.STR_BLANK :
                        DateUtil.convertDate2Str(pollEntity.getCreateTime(),DateUtil.FORMATE_YYYY_MM_DD_HH_MM_SS_MINUS));
                obj.put("pollName",StringUtils.isEmpty(pollEntity.getName()) ? Constants.STR_BLANK : pollEntity.getName());
                obj.put("groupName",StringUtils.isEmpty(pollEntity.getSgNames()) ? Constants.STR_BLANK : pollEntity.getSgNames());
                obj.put("pollId",StringUtils.isEmpty(pollEntity.getId()) ? Constants.STR_BLANK : pollEntity.getId());
                pollJar.add(obj);
            }
        }
        ret.put("pollList",pollJar);
        ret.put("totalCount",pollList.getTotal());
        //参数校验
        return RespMessage.genSuccessWithData(ret);
    }

    public RespMessage createPoll(JSONObject reqJo, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String pollName = CheckUtil.checkParamSimpleFromTo(ConstantsOfParamName.POLLNAME, ConstantsOfParamName.POLLNAME_ALIAS, String.class, reqJo, null, false, null, 1, 20,null);
        JSONArray pollProductJar =reqJo.getJSONArray("proArray");
        JSONArray sgIds =reqJo.getJSONArray("sgIds");
        if( sgIds.isEmpty() ){
            throw new ApiBizException(MsgCode.C00000040.code,"请选择组");
        }else  if (pollProductJar.isEmpty()){
            throw new ApiBizException(MsgCode.C00000040.code,"员工权益code标识,或者权益数量不能为空");

        }

        Long companyIdSg = TransferUtil.parseUserInfoFromJson(reqJo).getCompanyId();
        Long userId = TransferUtil.parseUserInfoFromJson(reqJo).getId();
        List<Long> objects = sgIds.toJavaList(Long.class);
        List<PollProductExt> pollProductExtList = pollProductJar.toJavaList(PollProductExt.class);

        Long pollId = pollModuleService.createPoll(companyIdSg, userId, pollName, objects, pollProductExtList);

        JSONObject ret = new JSONObject();
        ret.put("pollId",pollId);

        return  RespMessage.genSuccessWithData(ret);
    }


    public RespMessage queryProductList(JSONObject reqJo, HttpServletRequest request, HttpServletResponse response) throws Exception {

        JSONArray groupJar = new JSONArray();
        //组列表
        List<StaffGpEntity>  gpList = pollModuleService.queryGroupList(TransferUtil.parseUserInfoFromJson(reqJo).getCompanyId());
        for (StaffGpEntity staffGpEntity : gpList) {
            JSONObject stffObj = new JSONObject();
            stffObj.put("groupId",staffGpEntity.getIdSg());
            stffObj.put("groupName",staffGpEntity.getNameSg());
            stffObj.put("groupNum",staffGpEntity.getMemberNumSg());
            groupJar.add(stffObj);
        }

        JSONArray prdJar = new JSONArray();
        //产品列表
        List<ProductEntity>  prdList = pollModuleService.queryProductList();
        for (ProductEntity productEntity : prdList) {
            JSONObject obj = new JSONObject();
            obj.put("productCodeP",StringUtils.isEmpty(productEntity.getCode()) ? Constants.STR_BLANK : productEntity.getCode());
            obj.put("productName",StringUtils.isEmpty(productEntity.getName()) ? Constants.STR_BLANK : productEntity.getName());
            obj.put("productUnit",StringUtils.isEmpty(productEntity.getUnit()) ? Constants.STR_BLANK : productEntity.getUnit());
            obj.put("productNameExt",StringUtils.isEmpty(productEntity.getNameExt()) ? Constants.STR_BLANK : productEntity.getNameExt());
            prdJar.add(obj);
        }

        JSONObject ret = new JSONObject();
        ret.put("productList",prdJar);
        ret.put("groupList",groupJar);

        return  RespMessage.genSuccessWithData(ret);
    }

    /**
     * 调研详情
     * @param reqJo
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public RespMessage pollDetail(JSONObject reqJo, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Long pollId = CheckUtil.checkParamSimpleFromTo(ConstantsOfParamName.POLLID, ConstantsOfParamName.POLLId_ALIAS, Long.class, reqJo, null, false, null, null, null, null);

        //调研详情
        PollEntityExt pollDetail = pollModuleService.pollDetail(TransferUtil.parseUserInfoFromJson(reqJo).getCompanyId(), pollId);
        if (pollDetail == null) {
            throw new ApiBizException(MsgCode.C00000040.code, "调研表不存在");
        }

        JSONObject ret = new JSONObject();
        ret.put("pollName",StringUtils.isEmpty(pollDetail.getName()) ? Constants.STR_BLANK : pollDetail.getName());
        ret.put("chooseGroupNum",pollDetail.getSgMemberNum());

        //组
        JSONArray groupJar = new JSONArray();
        for (PollSgSnpEntity sgSnpEntity : pollDetail.getSgList()) {
            groupJar.add(sgSnpEntity.getSgNamePss());
        }
        ret.put("groupNameList",groupJar);

        //商品
        JSONArray productJar = new JSONArray();
        for (PollPrdSnpEntity pollPrdSnpEntity : pollDetail.getPrdList()) {
            JSONObject productObj = new JSONObject();
            productObj.put("productCode",StringUtils.isEmpty(pollPrdSnpEntity.getPrdCodePps()) ? Constants.STR_BLANK :pollPrdSnpEntity.getPrdCodePps());
            productObj.put("productName",StringUtils.isEmpty(pollPrdSnpEntity.getPrdNamePps()) ? Constants.STR_BLANK :pollPrdSnpEntity.getPrdNamePps());
            productObj.put("productNameExt",StringUtils.isEmpty(pollPrdSnpEntity.getPrdNameExtPps()) ? Constants.STR_BLANK :pollPrdSnpEntity.getPrdNameExtPps());
            productObj.put("productUnit",StringUtils.isEmpty(pollPrdSnpEntity.getPrdUnitPps()) ? Constants.STR_BLANK :pollPrdSnpEntity.getPrdUnitPps());
            productObj.put("productNum",pollPrdSnpEntity.getPurcsNumPps());
            productJar.add(productObj);
        }
        ret.put("productList",productJar);

        return RespMessage.genSuccessWithData(ret);
    }

    public RespMessage deletPoll(JSONObject reqJo, HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONArray pollIds = reqJo.getJSONArray("pollIds");
        List<Long> poIds = new ArrayList<>();
        if (StringUtils.isEmpty(pollIds)){
            throw new ApiBizException(MsgCode.C00000040.code,"请选择要删除的调研");
        }
        for (Object poId:pollIds){
            Long groupId = Long.valueOf(poId.toString()) ;
            poIds.add(groupId);
        }
        Long companyIdSg = TransferUtil.parseUserInfoFromJson(reqJo).getCompanyId();
        pollModuleService.deletPoll(poIds,companyIdSg);
        return RespMessage.genSuccess();
    }



}
