package com.poll.service.module;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.poll.common.Constants;
import com.poll.common.MsgCode;
import com.poll.common.exception.ApiBizException;
import com.poll.dao.mapper.StaffGpMapper;
import com.poll.dao.mapper.StaffMapper;
import com.poll.dao.service.GroupsService;
import com.poll.dao.service.StaffService;
import com.poll.entity.StaffEntity;
import com.poll.entity.StaffGpEntity;
import com.poll.entity.ext.StaffEntityExt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;

@Service
public class GroupsModuleService {

    protected Logger log = LogManager.getLogger();
    @Autowired
    private GroupsService groupsService;
    @Autowired
    private StaffService staffService;
    @Autowired
    private StaffMapper staffMapper;
    @Autowired
    private StaffGpMapper staffGpMapper;
    @Autowired
    private StaffModuleService staffModuleService;
    /**
     * 查询组列表
     * @param companyIdSg 企业id
     * @return
     */
   public Page<StaffGpEntity> groupsList (Long companyIdSg, int currentPage, int pageSize) throws  Exception{

       Wrapper<StaffGpEntity> wrapper = new EntityWrapper<>();
       wrapper.eq("company_id_sg",companyIdSg);
       wrapper.orderBy("id_sg",false);

       return groupsService.selectPage(new Page(currentPage,pageSize), wrapper);
   }

    /**
     * 创建组
     * @param companyIdSg
     * @param userId
     * @param groupName
     * @return
     * @throws Exception
     */
    public StaffGpEntity creatGroup (Long companyIdSg, Long userId, String groupName) throws  Exception{

        StaffGpEntity groupsEntity = new StaffGpEntity();
        groupsEntity.setCompanyIdSg(companyIdSg);
        groupsEntity.setUserIdSg(userId);
        groupsEntity.setNameSg(groupName);
        try{
            groupsService.insert(groupsEntity);
        }catch (Exception e){
            e.printStackTrace();
            if (e instanceof DuplicateKeyException) {
                DuplicateKeyException dupe = (DuplicateKeyException)e;
                if (dupe.getMessage().contains("Duplicate entry ")){
                    throw new ApiBizException(MsgCode.C00000040.code, "组名重复");
                }
            }
            throw new ApiBizException(MsgCode.C00000040.code, e.getMessage());
        }

        return groupsEntity;
    }

    /**
     * 删除组
     * @param companyIdSg
     * @param sgIds
     * @throws Exception
     */
    public  void deletGroup (Long companyIdSg,List<Long> sgIds) throws  Exception{
        for (Long sgId:sgIds) {
            StaffGpEntity groupsEntity =queryByGroupId(companyIdSg,sgId);
            if (null == groupsEntity ){
                throw  new ApiBizException(MsgCode.C00000040.code,"该组已不存在");
            }else if (groupsEntity.getTypeSg().equals(Constants.BYTE0)){
                throw  new ApiBizException(MsgCode.C00000040.code,"默认组不允许删除");
            }else  if (groupsEntity.getMemberNumSg() > 0){
                throw  new ApiBizException(MsgCode.C00000040.code,"所选组:"+groupsEntity.getNameSg()+"人数不为0，无法删除");
            }

        }

        boolean isDel = groupsService.deleteBatchIds(sgIds);
        if (!isDel){
            throw  new ApiBizException(MsgCode.C00000040.code,"删除失败");
        }
    }

    /**
     * 通过组名查找组
     * @param companyIdSg
     * @param groupName
     * @return
     * @throws Exception
     */
    public  StaffGpEntity queryByGroupName (Long companyIdSg,String groupName) throws  Exception{
        Wrapper<StaffGpEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("name_sg",groupName);
        wrapper.eq("company_id_sg",companyIdSg);
        StaffGpEntity group = groupsService.selectOne(wrapper);
        return group;
    }
    /**
     * 通过组Id查找组
     * @param companyIdSg
     * @param groupId
     * @return
     * @throws Exception
     */
    public  StaffGpEntity queryByGroupId (Long companyIdSg,Long groupId) throws  Exception{
        Wrapper<StaffGpEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("company_id_sg",companyIdSg);
        wrapper.eq("id_sg",groupId);
        StaffGpEntity group = groupsService.selectOne(wrapper);
        return group;
    }

    /**
     *
     * @param companyIdSg
     * @param groupId
     * @param newGroupName
     * @return
     * @throws Exception
     */
    public  void updateGroup (Long companyIdSg,Long groupId,String newGroupName) throws  Exception{
        StaffGpEntity groupsEntity = queryByGroupId(companyIdSg,groupId);
        if ( null == groupsEntity ){
            throw new ApiBizException(MsgCode.C00000040.code,"修改异常，该组已不存在");
        }else if (groupsEntity.getTypeSg().equals(Constants.BYTE0)){
            throw new ApiBizException(MsgCode.C00000040.code,"修改异常，默认组无法修改");
        }
        Wrapper<StaffGpEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("id_sg",groupsEntity.getIdSg());
        wrapper.eq("company_id_sg",companyIdSg);
        StaffGpEntity group = new StaffGpEntity();
        group.setNameSg(newGroupName);
        group.setUpdateTimeSg(new Date());
       try{
           groupsService.update(group,wrapper);
       }catch (Exception e){
           e.printStackTrace();
           if (e instanceof DuplicateKeyException) {
               DuplicateKeyException dupe = (DuplicateKeyException)e;
               if (dupe.getMessage().contains("Duplicate entry ")){
                   throw new ApiBizException(MsgCode.C00000040.code, "组名重复");
               }
           }
           throw new ApiBizException(MsgCode.C00000040.code, e.getMessage());
       }

    }

    /**
     * 组成员列表
     * @param companyIdSg
     * @param mobile
     * @param userName
     * @return
     * @throws Exception
     */
    public  Page<StaffEntity> groupMemberList (Long groupId,Long companyIdSg, Long mobile, String userName,int current, int size) throws  Exception{
       Page<StaffEntity> list = null;
        Wrapper<StaffEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("company_id",companyIdSg);
        wrapper.eq("sg_id",groupId);
        wrapper.orderBy("create_time",false);
        if (null!=mobile){
            wrapper.eq("mobile",mobile);
        }if (null !=userName ){
            wrapper.eq("name",userName);
        }
        Page page = new Page(current,size);
        try{
            list = staffService.selectPage(page,wrapper);
        }catch (Exception e){
            e.printStackTrace();
            throw new ApiBizException(MsgCode.C00000040.code,"查询失败");
        }
        return  list;
    }
    /**
     * 根据员工手机号查询员工
     * @param mobile
     * @return
     * @throws ApiBizException
     */
    public StaffEntity findByMobile(Long mobile) throws ApiBizException {
        Wrapper<StaffEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("mobile",mobile);
        StaffEntity staffEntity= null;
        try{
            staffEntity = staffService.selectOne(wrapper);
       } catch (Exception e){
           e.printStackTrace();
           throw  new ApiBizException(MsgCode.C00000040.code,"通过手机号查询员工异常");
       }
       return staffEntity;
    }


    /**
     * 修改分组成员信息
     * @param uId
     * @param companyIdSg
     * @param mobile
     * @param name
     * @param memo
     * @throws Exception
     */
    public  void updateGroupMember (Long uId, Long companyIdSg, Long mobile, String name, String memo) throws  Exception{
        StaffEntity staff = staffService.selectById(uId);
        if (staff==null){
            throw  new ApiBizException(MsgCode.C00000040.code,"无当前员工信息");
        }
        Wrapper<StaffEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("company_id",companyIdSg);
        wrapper.eq("id",uId);
        StaffEntity staffEntity = new StaffEntity();
        staffEntity.setMobile(mobile);
        staffEntity.setName(name);
        if (null !=memo){
            staffEntity.setRemark(memo);
        }
        try{
            staffService.update(staffEntity,wrapper);
        }catch (Exception e){
            e.printStackTrace();
            if (e instanceof DuplicateKeyException) {
                DuplicateKeyException dupe = (DuplicateKeyException)e;
                if (dupe.getMessage().contains("Duplicate entry ")){
                    throw new ApiBizException(MsgCode.C00000040.code, "手机号码重复");
                }
            }
            throw new ApiBizException(MsgCode.C00000040.code, e.getMessage());
        }
    }

    /**
     * 通过员工id查找所在组名
     * @param uId
     * @param companyIdSg
     * @return
     * @throws ApiBizException
     */
    public StaffEntityExt findGroupByUid(Long uId, Long companyIdSg) throws ApiBizException {

        StaffEntityExt staffEntity= null;
        try{
            staffEntity = staffMapper.findGroupByUid(uId,companyIdSg);
        } catch (Exception e){
            e.printStackTrace();
            throw  new ApiBizException(MsgCode.C00000040.code,"通过员工id查找所在组信息异常");
        }
        return staffEntity;
    }

    @Transactional(rollbackFor = Exception.class)
    public void groupMemberAdjust(List<Long> staffIdList, Long companyIdSg, Long targetGroupId) throws Exception {

        Map<Long, ArrayList<Long>> idListMap = constructIdListMapByGroup(staffIdList, companyIdSg);

        int total = 0;
        Iterator<Map.Entry<Long, ArrayList<Long>>> iterator = idListMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, ArrayList<Long>> entry = iterator.next();

            //转移员工
            Integer updateNum = staffMapper.updateBatchGroup(entry.getValue(), companyIdSg, targetGroupId, targetGroupId);

            if (updateNum > 0) {
                //维护员工旧组数量
                staffGpMapper.decreaseGpMemberNum(companyIdSg, entry.getKey(), updateNum);

                //统计员工新组数量
                total += updateNum;
            }
        }

        if (total > 0) {
            staffGpMapper.addGpMemberNumGpMemberNum(companyIdSg, targetGroupId, total);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deletgroupMember(List<Long> gpUIds, Long companyIdSg) throws Exception {

        Map<Long, ArrayList<Long>> idListMap = constructIdListMapByGroup(gpUIds, companyIdSg);

        Iterator<Map.Entry<Long, ArrayList<Long>>> iterator = idListMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, ArrayList<Long>> entry = iterator.next();
            //删除员工
            Integer deleteNum = staffMapper.deletBatchGroupNum(entry.getValue(), companyIdSg);
            if (deleteNum > 0) {
                //员工对应组成员数量扣减
                staffGpMapper.decreaseGpMemberNum(companyIdSg, entry.getKey(), deleteNum);
            }
        }
    }

    //查询组列表
    public List<StaffGpEntity> selectGroupList(Long companyIdSg) throws  Exception {
        List<StaffGpEntity> staffGpList = null;
        try{
            Wrapper<StaffGpEntity> wrapper = new EntityWrapper<>();
            wrapper.eq("company_id_sg",companyIdSg);
            staffGpList = staffGpMapper.selectList(wrapper);
        }catch ( Exception e){
            throw  new ApiBizException(MsgCode.C00000040.code,"组列表查询失败");
        }
        return  staffGpList;
    }

    /**
     * 返回员工根据组别分布的map
     * @param staffIdList
     * @param companyIdSg
     * @return
     */
    private Map<Long, ArrayList<Long>> constructIdListMapByGroup(List<Long> staffIdList, Long companyIdSg) {

        Map<Long, ArrayList<Long>> idListMap = new HashMap<>();

        //查询员工
        List<StaffEntityExt> staffList = staffMapper.staffGroupList(companyIdSg, staffIdList);
        for (StaffEntityExt staff : staffList) {
            ArrayList<Long> idList = idListMap.get(staff.getSgId());
            if (idList == null) {
                idList = new ArrayList<>();
                idListMap.put(staff.getSgId(), idList);
            }
            idList.add(staff.getId());
        }
        return idListMap;
    }

}
