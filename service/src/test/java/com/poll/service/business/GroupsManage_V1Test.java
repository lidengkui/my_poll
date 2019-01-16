package com.poll.service.business;


import com.poll.ability.dto.RespMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GroupsManage_V1Test {

    @Autowired
    private  GroupsManageService_V1 groupsManage_v1;
    @Test
    public void groupsList() {

        RespMessage groupsList = null;
        try {
            groupsList = groupsManage_v1.groupsList(null,null,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("==============="+groupsList.constrctRespJo().toJSONString());
    }
}
