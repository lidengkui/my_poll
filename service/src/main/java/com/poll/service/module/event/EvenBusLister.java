package com.poll.service.module.event;

import com.poll.common.MsgCode;
import com.poll.common.exception.ApiBizException;
import com.poll.entity.StaffGpEntity;
import com.poll.service.module.GroupsModuleService;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Invoke;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author lidengkui
 * @className EvenBusLister
 * @description
 * @date 2019/1/14 0014
 */
@EventHandler
public class EvenBusLister{
    @Autowired
    private GroupsModuleService groupsModuleService;

    @Handler(delivery = Invoke.Asynchronously)
   // @Enveloped(messages = {StaffGpEntity.class})
    @Transactional(propagation=Propagation.REQUIRED)
    public  void test( StaffGpEntity groupsEntity) throws  Exception{
        try {
            System.out.println("==========================================发送短信2：");
            StaffGpEntity staffGp = groupsModuleService.queryByGroupId(groupsEntity.getCompanyIdSg(),groupsEntity.getIdSg());
           System.out.println("=============================staffGp:"+staffGp);
            //messageModuleService.sendSms(159L,"test111", MessageModuleService.SmsTempltCodeEnum.resetPassword,"1111");
            System.out.println("=============================事件消费2");
            groupsModuleService.creatGroup(groupsEntity.getCompanyIdSg(),groupsEntity.getUserIdSg(),"eventBusLister1233");
        } catch (Exception e) {
            e.printStackTrace();
            throw  new ApiBizException(MsgCode.C00000040.code,MsgCode.C00000040.msg);
        }
    }
}
