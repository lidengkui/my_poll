package com.poll.service.module;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.poll.ability.dto.RespMessage;
import com.poll.common.Constants;
import com.poll.common.MsgCode;
import com.poll.common.exception.ApiBizException;
import com.poll.dao.service.StaffService;
import com.poll.entity.StaffEntity;
import com.poll.entity.StaffGpEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GroupsModuleServiceTest {
    @Autowired
    private  GroupsModuleService groupsModuleService;
    @Autowired
    private StaffService staffService;

    @Test
    public void groupsList(){
        try {
           Page page = groupsModuleService.groupsList(1l,1,10);
            JSONArray groupsJar = new JSONArray();
            JSONObject ret = new JSONObject();
            for (Object groupsEntityObj: page.getRecords()) {
                JSONObject obj = new JSONObject();
                StaffGpEntity groupsEntity = (StaffGpEntity) groupsEntityObj;
                obj.put("groupName",StringUtils.isEmpty(groupsEntity.getNameSg()) ? Constants.STR_BLANK : groupsEntity.getNameSg());
                obj.put("groupNum",groupsEntity.getMemberNumSg());
                obj.put("createDate",groupsEntity.getCreateTimeSg());
                groupsJar.add(obj);
            }
            ret.put("groupsList",groupsJar);
            ret.put("totalCount",page.getTotal());
            System.out.println(JSONObject.toJSONString("----------------list:"+page.getRecords()));
            System.out.println(JSONObject.toJSONString("----------------current:"+page.getTotal()));
            System.out.println("===============data:"+ RespMessage.genSuccessWithData(ret).getData());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Test
    public void creatGroup(){
        try {
            groupsModuleService.creatGroup(1L,1L,"技术部");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    public void updateGroup(){
        try {
            groupsModuleService.updateGroup(2L,8L,"高层");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Test
    public void deletGroup(){
        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray.add("11");
            jsonArray.add("12");
            List<Long> groupIds = new ArrayList<>();
            for (Object sgId:jsonArray){
                Long groupId = Long.valueOf(sgId.toString());
                groupIds.add(groupId);
            }
            if (StringUtils.isEmpty(jsonArray)){
                throw new ApiBizException(MsgCode.C00000040.code,"员工组Id不能为空");
            }
            groupsModuleService.deletGroup(2L,groupIds);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Test
    public void groupMemberList(){
        try {
           Page<StaffEntity> list = groupsModuleService.groupMemberList(6L,1L,null,null,1,2);
            JSONArray groupsJar = new JSONArray();
            JSONObject ret = new JSONObject();
            for (Object groupsEntityObj: list.getRecords()) {
                JSONObject obj = new JSONObject();
                StaffEntity staffEntity = (StaffEntity) groupsEntityObj;
                obj.put("codeS",StringUtils.isEmpty(staffEntity.getCode()) ? Constants.STR_BLANK : staffEntity.getCode());
                obj.put("name",StringUtils.isEmpty(staffEntity.getName()) ? Constants.STR_BLANK : staffEntity.getName());
                obj.put("createDate",staffEntity.getCreateTime());
                obj.put("mobile",staffEntity.getMobile());
                obj.put("memo",staffEntity.getRemark());
                groupsJar.add(obj);
            }
            ret.put("groupMemberList",groupsJar);
            ret.put("totalCount",list.getTotal());
            System.out.println("===============data:"+ RespMessage.genSuccessWithData(ret).getData());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Test
    public void groupMemberAdjust(){
        try {
            List<Long> groupIds = new ArrayList<>();
            JSONArray jsonArray = new JSONArray();
            jsonArray.add("6");
            jsonArray.add("7");

            for (Object sgId:jsonArray){
                Long groupId = Long.valueOf(sgId.toString());
                groupIds.add(groupId);
            }
            if (StringUtils.isEmpty(jsonArray)){
                throw new ApiBizException(MsgCode.C00000040.code,"员工组Id不能为空");
            }
            groupsModuleService.groupMemberAdjust(groupIds,1L,1L);

        } catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace();
        }

    }
}
