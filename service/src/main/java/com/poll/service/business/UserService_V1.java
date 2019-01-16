package com.poll.service.business;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.poll.ability.dto.RespMessage;
import com.poll.common.CodeContants;
import com.poll.common.Constants;
import com.poll.common.ConstantsOfParamName;
import com.poll.common.MsgCode;
import com.poll.common.exception.ApiBizException;
import com.poll.common.util.CheckUtil;
import com.poll.common.util.RegularUtil;
import com.poll.common.util.StringUtil;
import com.poll.dao.service.CompanyService;
import com.poll.dao.service.StaffGpService;
import com.poll.dao.service.UserService;
import com.poll.entity.CompanyEntity;
import com.poll.entity.StaffGpEntity;
import com.poll.entity.UserEntity;
import com.poll.entity.ext.UserEntityExt;
import com.poll.service.annotation.TokenLess;
import com.poll.service.module.TokenModuleService;
import com.poll.service.module.UserModuleService;
import com.google.common.base.Strings;
import com.poll.service.util.TransferUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;


@Service
public class UserService_V1 {

    protected Logger log = LogManager.getLogger();

    @Autowired
    protected UserModuleService userModuleService;

    @Autowired
    protected TokenModuleService tokenModuleService;

    @Autowired
    protected CompanyService companyService;

    @Autowired
    protected StaffGpService staffGpService;

    @Autowired
    protected UserService userService;

    /**
     * 登录
     *
     * @param reqJo
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @TokenLess
    public RespMessage login(JSONObject reqJo, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //账号
        String account = CheckUtil.checkParamSimpleFromTo(ConstantsOfParamName.ACCOUNT, ConstantsOfParamName.ACCOUNT_ALIAS, String.class, reqJo, null, false, null, 6, 19, RegularUtil.userNameReg);
        //密码
        String password = CheckUtil.checkParamSimpleFromTo(ConstantsOfParamName.PASSWORD, ConstantsOfParamName.PASSWORD_ALIAS, String.class, reqJo, null, false, null, 32, 32, "[a-fA-F0-9]{32}");
        //(图形)验证码
        String veriCode = CheckUtil.checkParamSimpleFromTo(ConstantsOfParamName.VERI_CODE, ConstantsOfParamName.VERI_CODE_ALIAS, String.class, reqJo, null, false, null, 4, 4, "[a-zA-Z0-9]{4}");
        //serializeStr
        String serializeStr = CheckUtil.checkParamSimpleFromTo(ConstantsOfParamName.SERIALIZESTR, ConstantsOfParamName.SERIALIZESTR_ALIAS, String.class, reqJo, null, false, null, 36, 36, "[-a-fA-F0-9]{36}");

        //登录校验
        UserEntity user = userModuleService.loginValid(account, password, veriCode, serializeStr);

        //生成token
        String token = tokenModuleService.genAccessToken4User(user.getId(), user.getCompanyId(), user.getMobile(), user.getTokenFlag(), "", null);
        JSONObject retJo = new JSONObject();
        retJo.put("token", token);

        //设置cookie
        Cookie cookie = new Cookie(ConstantsOfParamName.POLL_TOKEN, token);
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        response.addCookie(cookie);

        return RespMessage.genSuccessWithData(retJo);
    }

    /**
     * 图形验证码
     *
     * @param reqJo
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @TokenLess
    public void kaptcha(JSONObject reqJo, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String serializeStr = request.getParameter(ConstantsOfParamName.SERIALIZESTR);
        //serializeStr
        if (Strings.isNullOrEmpty(serializeStr)) {
            throw new ApiBizException(MsgCode.C00000001.code, "【" + ConstantsOfParamName.SERIALIZESTR_ALIAS + "】不能为空");
        }
        userModuleService.genKaptcha(response, serializeStr);
    }



    /**
     * 修改密码
     *
     * @param reqJo
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public RespMessage pwdChange(JSONObject reqJo, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //原秘密
        String oldPassword = CheckUtil.checkParamSimpleFromTo(ConstantsOfParamName.OLD_PASSWORD, ConstantsOfParamName.OLD_PASSWORD_ALIAS, String.class, reqJo, null, false, null, 32, 32, "[a-fA-F0-9]{32}");
        //新密码
        String password = CheckUtil.checkParamSimpleFromTo(ConstantsOfParamName.PASSWORD, ConstantsOfParamName.PASSWORD_ALIAS, String.class, reqJo, null, false, null, 32, 32, "[a-fA-F0-9]{32}");

        userModuleService.updatePwdByOldPwd(TransferUtil.parseUserInfoFromJson(reqJo).getId(), oldPassword, password);
        return RespMessage.genSuccess();
    }

    /**
     * 基础信息
     *
     * @param reqJo
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public RespMessage info(JSONObject reqJo, HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserEntity currentUser = TransferUtil.parseUserInfoFromJson(reqJo);
        JSONObject retJo = new JSONObject();

        UserEntity selectUser = userService.selectOne(new EntityWrapper<UserEntity>().eq("id_u", currentUser.getId()));
        if (selectUser != null) {
            JSONObject userDataJo = new JSONObject();
            retJo.put("user", userDataJo);
            userDataJo.put("id", currentUser.getId());
            userDataJo.put("name", selectUser.getUserName());
        }

        JSONObject companyDataJo= new JSONObject();
        retJo.put("company", companyDataJo);
        companyDataJo.put("id", currentUser.getCompanyId());
        companyDataJo.put("name", Constants.STR_BLANK);
        CompanyEntity companyEntity = companyService.selectById(currentUser.getCompanyId());
        if (companyEntity != null) {
            companyDataJo.put("name", StringUtils.isBlank(companyEntity.getName()) ? Constants.STR_BLANK : companyEntity.getName());
        }

        List<StaffGpEntity> staffGpEntityList = staffGpService.selectList(new EntityWrapper<StaffGpEntity>().eq("company_id_sg", currentUser.getCompanyId()).orderDesc(Arrays.asList("id_sg")));
        JSONArray staffGroupArray = new JSONArray();
        if (null != staffGpEntityList && !staffGpEntityList.isEmpty()) {
            for (StaffGpEntity group : staffGpEntityList) {
                JSONObject tmpJo = new JSONObject();
                tmpJo.put("id", group.getIdSg());
                tmpJo.put("name", group.getNameSg());
                staffGroupArray.add(tmpJo);
            }
        }
        companyDataJo.put("group", staffGroupArray);
        return RespMessage.genSuccessWithData(retJo);
    }

}
