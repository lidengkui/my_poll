package com.poll.service.module.event;

import com.poll.common.MsgCode;
import com.poll.common.exception.ApiBizException;
import com.poll.entity.StaffEntity;
import com.poll.service.module.GroupsModuleService;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Invoke;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author lidengkui
 * @className EvenBusLister2
 * @description
 * @date 2019/1/15 0015
 */
@EventHandler
public class EvenBusLister2 {
    @Autowired
    private GroupsModuleService groupsModuleService;


    @Handler(delivery = Invoke.Asynchronously)
    // @Enveloped(messages = {StaffGpEntity.class})
    public  void test( StaffEntity groupsEntity) throws  Exception{
        try {
            System.out.println("==========================================发送短信1：");
           // messageModuleService.sendSms(1592L,"test222", MessageModuleService.SmsTempltCodeEnum.resetPassword,"2222");
            System.out.println("=============================时间消费1");

            groupsModuleService.creatGroup(12L,4L,"eventBusLister000");
        } catch (Exception e) {
            e.printStackTrace();
            throw  new ApiBizException(MsgCode.C00000040.code,MsgCode.C00000040.msg);
        }
    }
}
