package com.poll.service.business;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.poll.ability.dto.RespMessage;
import com.poll.common.Constants;
import com.poll.common.util.CheckUtil;
import com.poll.common.util.MD5Util;
import com.poll.common.util.RandomUtil;
import com.poll.common.util.RegularUtil;
import com.poll.dao.service.CompanyService;
import com.poll.dao.service.StaffGpService;
import com.poll.dao.service.UserService;
import com.poll.entity.CompanyEntity;
import com.poll.entity.StaffGpEntity;
import com.poll.entity.UserEntity;
import com.poll.redis.RedisService;
import com.poll.service.annotation.TokenLess;
import com.poll.service.module.UserModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.List;


@Service
public class AssistService_V1 {

    @Autowired
    private RedisService redisService;


    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserModuleService userModuleService;

    @Autowired
    private StaffGpService staffGpService;


    @TokenLess
    public RespMessage getRedisValue(JSONObject reqJo, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String key = CheckUtil.checkParamSimpleFromTo("key", "key", String.class, request, null, false, null, 1, 100, null);
        String serializer = CheckUtil.checkParamSimpleFromTo("serializer", "serializer", String.class, request, null, true, "jdk", 1, 100, null);

        String data = null;

        RespMessage respMsg = RespMessage.genSuccess();

        if (serializer.equals("jdk")) {
            Object value = redisService.getValue(key, redisService.getJdkSerializationRedisSerializer());
            data = JSONObject.toJSONString(value);
        } else if (serializer.equals("jackson")) {
            Object value = redisService.getValue(key, redisService.getJackson2JsonRedisSerializer());
            data = JSONObject.toJSONString(value);
        } else {
            Object value = redisService.getValue(key, redisService.getStringRedisSerializer());
            if (value != null) {
                data = value.toString();
            }
        }

        respMsg.setData(data);

        return respMsg;
    }

    @TokenLess
    public RespMessage delRedisKey(JSONObject reqJo, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String key = CheckUtil.checkParamSimpleFromTo("key", "key", String.class, request, null, false, null, 1, 100, null);

        redisService.delKey(key);

        return RespMessage.genSuccess();
    }
    @TokenLess
    public RespMessage genUser(JSONObject reqJo, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String userName = CheckUtil.checkParamSimpleFromTo("userName", null, String.class, request, null, false, null, 1, 40, null);
        String password = CheckUtil.checkParamSimpleFromTo("password", null, String.class, request, null, false, null, 1, 40, null);
        String companyName = CheckUtil.checkParamSimpleFromTo("companyName", null, String.class, request, null, false, null, 1, 400, null);
        companyName = URLDecoder.decode(companyName, Constants.CHARSET_UTF8);
        Long mobile = CheckUtil.checkParamSimpleFromTo("mobile", null, Long.class, request, null, true, null, 1, 40, RegularUtil.pureNumReg);
        String email = CheckUtil.checkParamSimpleFromTo("email", null, String.class, request, null, true, null, 1, 40, null);

        Long companyId = null;
        Wrapper<CompanyEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("name_c", companyName);
        List<CompanyEntity> companyList = companyService.selectList(wrapper);
        if (companyList.size() < 1) {
            //创建公司
            CompanyEntity companyEntity = new CompanyEntity();
            companyEntity.setName(companyName);
            companyService.insert(companyEntity);
            companyId = companyEntity.getId();
        } else {
            companyId = companyList.get(0).getId();
        }

        //创建用户
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(userName);
        String salt = RandomUtil.genLetterLowerNumStr(6);
        String pwd = userModuleService.handlePasswordWithSalt(MD5Util.encode(password).toLowerCase(), salt).toLowerCase();
        userEntity.setSalt(salt);
        userEntity.setPassword(pwd);
        userEntity.setCompanyId(companyId);
        userEntity.setMobile(mobile);
        userEntity.setEmail(email);
        userService.insert(userEntity);

        //判断是否有默认组
        Wrapper<StaffGpEntity> staffGpWrapper = new EntityWrapper<>();
        staffGpWrapper.eq("company_id_sg", companyId);
        staffGpWrapper.eq("type_sg", Constants.BYTE0);
        StaffGpEntity sgpEntity = staffGpService.selectOne(staffGpWrapper);
        if (sgpEntity == null) {
            sgpEntity = new StaffGpEntity();
            sgpEntity.setCompanyIdSg(companyId);
            sgpEntity.setUserIdSg(userEntity.getId());
            sgpEntity.setNameSg("默认组");
            sgpEntity.setTypeSg(Constants.BYTE0);
            staffGpService.insert(sgpEntity);
        }

        return RespMessage.genSuccess();
    }

}
