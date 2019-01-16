package com.poll.service.module;

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.poll.common.Constants;
import com.poll.common.MsgCode;
import com.poll.common.exception.ApiBizException;
import com.poll.common.util.DateUtil;
import com.poll.common.util.StringUtil;
import com.poll.dao.mapper.*;
import com.poll.dao.service.*;
import com.poll.entity.*;
import com.poll.entity.ext.PollEntityExt;
import com.poll.entity.ext.PollProductExt;
import com.poll.redis.RedisService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;


@Service
public class PollModuleService {
    protected Logger log = LogManager.getLogger();
    @Autowired
    private PollService pollService;
    @Autowired
    private ProductService productService;
    @Autowired
    private StaffGpMapper staffGpMapper;
    @Autowired
    private PollMapper pollMapper;
    @Autowired
    private PollPrdSnpMapper pollPrdSnpMapper;
    @Autowired
    private PollPrdSnpService pollPrdSnpService;
    @Autowired
    private PollSgSnpMapper pollSgSnpMapper;
    @Autowired
    private PollSgSnpService pollSgSnpService;
    @Autowired
    private PollSgStfPrdSnpMapper pollSgStfPrdSnpMapper;
    @Autowired
    private PollSgStfPrdSnpService pollSgStfPrdSnpService;
    @Autowired
    private StaffMapper staffMapper;
    @Autowired
    private RedisService redisService;

    private static final int BATCH_SIZE = 500;


    public Page<PollEntity> pollList(Long companyIdSg, Integer current, Integer size, String pollName, String pollCode,
                                     Date pollCreateStartTime, Date pollCreateEndTime) throws  Exception {

        Page<PollEntity> page = new Page<>(current,size);
        Page<PollEntity> pageList = null;
        try{
            Wrapper<PollEntity> wrapper = new EntityWrapper<>();
            wrapper.eq("company_id",companyIdSg);
            wrapper.orderBy("id",false);
            if (!StringUtils.isEmpty(pollName)){
                wrapper.like(true,"name",pollName,SqlLike.DEFAULT);
            }
            if (!StringUtils.isEmpty(pollCode)){
                wrapper.eq("code",pollCode);
            }
            if (!StringUtils.isEmpty(pollCreateStartTime)){
                wrapper.ge(true,"create_time",pollCreateStartTime);
            }
            if (!StringUtils.isEmpty(pollCreateEndTime)){
                wrapper.le(true,"create_time",DateUtil.addDay(pollCreateEndTime,1));
            }

            pageList = pollService.selectPage(page,wrapper);
        }catch (Exception e){
            log.info("调研列表查询失败",e.getMessage());
            throw  new ApiBizException(MsgCode.C00000040.code,"调研列表查询失败");
        }

        return  pageList;
    }
    public List<ProductEntity> queryProductList() throws  Exception {
        Wrapper<ProductEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("status",Constants.BYTE1);
        wrapper.orderBy("order_field");
        return productService.selectList(wrapper);
    }

    public List<StaffGpEntity> queryGroupList(Long companyId) throws Exception {

        if (companyId == null) {
            throw new ApiBizException(MsgCode.C00000011.code, MsgCode.C00000011.msg);
        }

        Wrapper<StaffGpEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("company_id_sg",companyId);
        wrapper.orderBy("id_sg", false);

        return staffGpMapper.selectList(wrapper);
    }

    public PollEntityExt pollDetail(Long companyIdSg, Long pollId) throws Exception {

        if (companyIdSg == null || pollId == null) {
            throw new ApiBizException(MsgCode.C00000011.code, MsgCode.C00000011.msg);
        }

        return pollMapper.queryDetailById(pollId, companyIdSg);
    }


    @Transactional(rollbackFor = Exception.class)
    public Long createPoll(Long companyIdSg,Long userId, String pollName, List<Long> groupIds, List<PollProductExt> productExtList) throws  Exception {

        //遍历商品得到 编码-数量 对照map
        Map<String, Integer> prdNumMap = new HashMap<>();
        for (PollProductExt pollProductExt : productExtList) {
            if (StringUtils.isEmpty(pollProductExt.getProductCodeP()) ){
                throw new ApiBizException(MsgCode.C00000011.code, "所选权益标识不能为空");
            }
            if (null == pollProductExt.getProductNum()|| pollProductExt.getProductNum() <= 0){
                throw new ApiBizException(MsgCode.C00000011.code, "所选权益数量必须大于0");
            }
            prdNumMap.put(pollProductExt.getProductCodeP(), pollProductExt.getProductNum());
        }

        Wrapper<ProductEntity> prdWrapper = new EntityWrapper<>();
        prdWrapper.in("code", prdNumMap.keySet());
        prdWrapper.orderBy("order_field");
        List<ProductEntity> prdList = productService.selectList(prdWrapper);
        if (prdList.size() != productExtList.size()) {
            throw new ApiBizException(MsgCode.C00000011.code, "存在非法权益标识");
        }

        //查询组列表
        Wrapper<StaffGpEntity> staffGpEntityWrapper = new EntityWrapper<>();
        staffGpEntityWrapper.eq("company_id_sg", companyIdSg);
        staffGpEntityWrapper.in("id_sg", groupIds);
        staffGpEntityWrapper.orderBy("id_sg", false);
        List<StaffGpEntity> groupList = staffGpMapper.selectList(staffGpEntityWrapper);
        if (groupList.size() != groupIds.size()) {
            throw new ApiBizException(MsgCode.C00000011.code, "存在非法分组标识");
        }

        StringBuilder pollGroupName = new StringBuilder();
        int gpMemberNum = 0;

        //校验组人数
        for (StaffGpEntity staffGp : groupList) {
            if (staffGp.getMemberNumSg() < 1){
                throw new ApiBizException(MsgCode.C00000040.code, "所选组："+staffGp.getNameSg()+" 人数为0，请重新选择");
            }

            //拼接组名
            if (pollGroupName.length() > 0) {
                pollGroupName.append(Constants.STR_COMMA);
            }
            pollGroupName.append(staffGp.getNameSg());

            gpMemberNum += staffGp.getMemberNumSg();
        }

        Date sysDate = new Date();

        //创建调研
        PollEntity pollEntity = new PollEntity();
        pollEntity.setCompanyId(companyIdSg);
        pollEntity.setUserId(userId);
        pollEntity.setCode(creatOrderNum(sysDate));
        pollEntity.setName(pollName);
        pollEntity.setSgNames(pollGroupName.toString());
        pollEntity.setSgMemberNum(gpMemberNum);
        pollEntity.setCreateTime(sysDate);
        pollEntity.setUpdateTime(sysDate);
        try {
            pollMapper.insert(pollEntity);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof DuplicateKeyException) {
                DuplicateKeyException dupe = (DuplicateKeyException)e;
                if (dupe.getMessage().contains("Duplicate entry ")){
                    throw new ApiBizException(MsgCode.C00000040.code, "创建调研失败：调研表编号重复");
                }
            }
            throw new ApiBizException(MsgCode.C00000040.code, e.getMessage());
        }

        //PollSgSnpEntity插入数据库后 组别id-当前记录id 对照map
        Map<Long, Long> sgIdDbIdMap = new HashMap<>();

        //插入关联用户组别数据
        List<PollSgSnpEntity> psslist = new ArrayList<>();
        for (StaffGpEntity staffGp : groupList) {
            PollSgSnpEntity pollSgSnp = new PollSgSnpEntity();
            pollSgSnp.setPollIdPss(pollEntity.getId());
            pollSgSnp.setCompanyIdPss(companyIdSg);
            pollSgSnp.setUserIdPss(userId);
            pollSgSnp.setSgIdPss(staffGp.getIdSg());
            pollSgSnp.setSgNamePss(staffGp.getNameSg());
            pollSgSnp.setSgMemberNumPss(staffGp.getMemberNumSg());
            pollSgSnp.setCreateTimePss(sysDate);
            psslist.add(pollSgSnp);

            if (psslist.size() >= BATCH_SIZE) {
                pollSgSnpService.insertBatch(psslist);
                for (PollSgSnpEntity pss : psslist) {
                    sgIdDbIdMap.put(pss.getSgIdPss(), pss.getIdPss());
                }
                psslist.clear();
            }
        }
        if (psslist.size() > 0) {
            pollSgSnpService.insertBatch(psslist);
            for (PollSgSnpEntity pss : psslist) {
                sgIdDbIdMap.put(pss.getSgIdPss(), pss.getIdPss());
            }
            psslist.clear();
        }

        //插入关联产品数据
        List<PollPrdSnpEntity> ppsList = new ArrayList<>();
        for (ProductEntity prd : prdList) {

            PollPrdSnpEntity pps = new PollPrdSnpEntity();
            pps.setPollIdPps(pollEntity.getId());
            pps.setCompanyIdPps(companyIdSg);
            pps.setUserIdPps(userId);
            pps.setPrdCodePps(prd.getCode());
            pps.setPrdNamePps(prd.getName());
            pps.setPrdNameExtPps(prd.getNameExt());
            pps.setPrdUnitPps(prd.getUnit());
            pps.setPrdOrderFieldPps(prd.getOrderField());
            pps.setPurcsNumPps(prdNumMap.get(prd.getCode()));
            pps.setCreateTimePps(sysDate);
            ppsList.add(pps);

            if (ppsList.size() >= BATCH_SIZE) {
                pollPrdSnpService.insertBatch(ppsList);
                ppsList.clear();
            }
        }
        if (ppsList.size() > 0) {
            pollPrdSnpService.insertBatch(ppsList);
            ppsList.clear();
        }

        //分页查询组内所有员工
        for (int offset = 0;;offset += BATCH_SIZE) {
            List<StaffEntity> list = staffMapper.selectListByCdt(companyIdSg, groupIds, "sg_id_s, id_s", offset, BATCH_SIZE);
            if (list.size() < 1) {
                break;
            }

            List<PollSgStfPrdSnpEntity> psspsList = new ArrayList<>();
            //插入调研、组、商品快照
            for (StaffEntity staff : list) {
                for (ProductEntity prd : prdList) {
                    PollSgStfPrdSnpEntity pssps = new PollSgStfPrdSnpEntity();
                    pssps.setPssIdPssps(sgIdDbIdMap.get(staff.getSgId()));
                    pssps.setPollIdPssps(pollEntity.getId());
                    pssps.setCompanyIdPssps(companyIdSg);
                    pssps.setUserIdPssps(userId);
                    pssps.setSgIdPssps(staff.getSgId());
                    pssps.setStfIdPssps(staff.getId());
                    pssps.setStfNamePssps(staff.getName());
                    pssps.setStfMobilePssps(staff.getMobile());
                    pssps.setPrdCodePssps(prd.getCode());
                    pssps.setPrdNamePssps(prd.getName());
                    pssps.setPrdNameExtPssps(prd.getNameExt());
                    pssps.setPrdUnitPssps(prd.getUnit());
                    pssps.setPrdOrderFieldPssps(prd.getOrderField());
                    pssps.setPurcsNumPssps(prdNumMap.get(prd.getCode()));
                    pssps.setCreateTimePssps(sysDate);
                    psspsList.add(pssps);

                    if (psspsList.size() >= BATCH_SIZE) {
                        pollSgStfPrdSnpService.insertBatch(psspsList);
                        psspsList.clear();
                    }
                }
            }
            if (psspsList.size() > 0) {
                pollSgStfPrdSnpService.insertBatch(psspsList);
                psspsList.clear();
            }
        }

        return pollEntity.getId();
    }

    public  List<ProductEntity> queryByProductCode(List<String> productCode)throws  Exception{
        List<ProductEntity> productEntityList = null;
        try{
            Wrapper<ProductEntity> wrapper = new EntityWrapper<>();
            wrapper.in(true,"code",productCode);
            wrapper.orderBy("order_field");
            productEntityList = productService.selectList(wrapper);
        }catch (Exception e){
            log.info("根据产品id查询列表失败",e.getMessage());
            throw  new ApiBizException(MsgCode.C00000040.code,"根据产品id查询列表失败");
        }
        return productEntityList;
    }

    /**
     * 删除调研
     * @param pollIds
     * @param companyIdSg
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public  void deletPoll(List<Long> pollIds,Long companyIdSg)throws  Exception{
        Wrapper<PollEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("company_id",companyIdSg);
        wrapper.in(true,"id",pollIds);
        // 调研表所选权益快照表
        Wrapper<PollPrdSnpEntity> prdSnpWrapper = new EntityWrapper<>();
        prdSnpWrapper.in(true,"poll_id_pps",pollIds);
        prdSnpWrapper.eq("company_id_pps",companyIdSg);
        //调研表所选组别快照表
        Wrapper<PollSgSnpEntity> sgSnpWrapper = new EntityWrapper<>();
        sgSnpWrapper.in(true,"poll_id_pss",pollIds);
        sgSnpWrapper.eq("company_id_pss",companyIdSg);
        //调研表所选组别下员工权益映射快照
        Wrapper<PollSgStfPrdSnpEntity> sgStfPrdSnpWrapper = new EntityWrapper<>();
        sgStfPrdSnpWrapper.in(true,"poll_id_pssps",pollIds);
        sgStfPrdSnpWrapper.eq("company_id_pssps",companyIdSg);
        try{
            pollMapper.delete(wrapper);
            pollPrdSnpMapper.delete(prdSnpWrapper);
            pollSgSnpMapper.delete(sgSnpWrapper);
            pollSgStfPrdSnpMapper.delete(sgStfPrdSnpWrapper);
        }catch (Exception e){
            throw  new ApiBizException(MsgCode.C00000040.code,"删除调研失败");

        }
    }

    /**
     * 生成订单号
     * @param sysDate
     * @return
     * @throws Exception
     */
    public String creatOrderNum(Date sysDate) throws Exception {

        String dateStr = DateUtil.convertDate2Str(sysDate, DateUtil.FORMATE_YYYYMMDD);
        String countStr = StringUtil.appendHead2Len(String.valueOf(redisService.count(Constants.POLL_ORDER_COUNT_KEY + dateStr, 1, -1, new Date(sysDate.getTime() + Constants.MILLS_DAY2), null)), Constants.FLAG_NO_CHAR, 5);

        return dateStr + countStr;
    }
    /**
     * 创建  调研表所选组别每组所选权益快照
     * @param pollId  调研id
     * @param companyIdSg  员工组
     * @param productEntity  权益/产品
     * @param purcsNum //选购数量
     * @return
     */

    public PollPrdSnpEntity creatPollPrdSnp(Long pollId,Long companyIdSg,Long userId,ProductEntity productEntity,Integer purcsNum ){
        PollPrdSnpEntity pollPrdSnpEntity = new PollPrdSnpEntity();
        pollPrdSnpEntity.setPollIdPps(pollId);
        pollPrdSnpEntity.setCompanyIdPps(companyIdSg);
        pollPrdSnpEntity.setUserIdPps(userId);
        pollPrdSnpEntity.setPrdCodePps(productEntity.getCode());
        pollPrdSnpEntity.setPrdNamePps(productEntity.getName());
        pollPrdSnpEntity.setPrdNameExtPps(productEntity.getNameExt());
        pollPrdSnpEntity.setPrdUnitPps(productEntity.getUnit());
        pollPrdSnpEntity.setPurcsNumPps(purcsNum);
        pollPrdSnpEntity.setCreateTimePps(new Date());
        pollPrdSnpMapper.insert(pollPrdSnpEntity);
        return  pollPrdSnpEntity;
    }

    /**
     *  创建 调研表所选组别映射快照
     * @param pollId  调研id
     * @param staffGpEntity 员工组
     * @return
     */
    public PollSgSnpEntity creatPollSgSnpEntity(Long pollId,StaffGpEntity staffGpEntity){
        PollSgSnpEntity pollSgSnpEntity = new PollSgSnpEntity();
        pollSgSnpEntity.setPollIdPss(pollId);
        pollSgSnpEntity.setSgIdPss(staffGpEntity.getIdSg());
        pollSgSnpEntity.setCompanyIdPss(staffGpEntity.getCompanyIdSg());
        pollSgSnpEntity.setUserIdPss(staffGpEntity.getUserIdSg());
        pollSgSnpEntity.setSgNamePss(staffGpEntity.getNameSg());
        pollSgSnpEntity.setSgMemberNumPss(staffGpEntity.getMemberNumSg());
        pollSgSnpMapper.insert(pollSgSnpEntity);
        return pollSgSnpEntity;
    }

    /**
     * 创建  调研表所选组别下员工权益映射快照
     * @param pssIdPssps  调研表所选组别映射快照Id
     * @param pollPrdSnpEntity 所选组别每组所选权益快照
     * @param staffEntity  员工
     * @param purcsNum  选购权益数量
     * @return
     */
    public PollSgStfPrdSnpEntity creatPollSgStfPrdSnpEntity(Long pollId, Long pssIdPssps, PollPrdSnpEntity pollPrdSnpEntity, StaffEntity staffEntity, Integer purcsNum){
        PollSgStfPrdSnpEntity pollSgStfPrdSnpEntity = new PollSgStfPrdSnpEntity();
        pollSgStfPrdSnpEntity.setPssIdPssps(pssIdPssps);
        pollSgStfPrdSnpEntity.setPollIdPssps(pollId);
        pollSgStfPrdSnpEntity.setCompanyIdPssps(staffEntity.getCompanyId());
        pollSgStfPrdSnpEntity.setUserIdPssps(staffEntity.getUserId());
        pollSgStfPrdSnpEntity.setSgIdPssps(staffEntity.getSgId());
        pollSgStfPrdSnpEntity.setStfIdPssps(staffEntity.getId());
        pollSgStfPrdSnpEntity.setStfNamePssps(staffEntity.getName());
        pollSgStfPrdSnpEntity.setStfMobilePssps(staffEntity.getMobile());
        pollSgStfPrdSnpEntity.setPrdCodePssps(pollPrdSnpEntity.getPrdCodePps());
        pollSgStfPrdSnpEntity.setPrdNamePssps(pollPrdSnpEntity.getPrdNamePps());
        pollSgStfPrdSnpEntity.setPrdNameExtPssps(pollPrdSnpEntity.getPrdNameExtPps());
        pollSgStfPrdSnpEntity.setPrdUnitPssps(pollPrdSnpEntity.getPrdUnitPps());
        pollSgStfPrdSnpEntity.setPurcsNumPssps(purcsNum);
        pollSgStfPrdSnpMapper.insert(pollSgStfPrdSnpEntity);
        return pollSgStfPrdSnpEntity;
    }


}
