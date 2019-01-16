package com.poll.service.module;

import com.alibaba.fastjson.JSONObject;
import com.poll.ability.dto.ReqMessage;
import com.poll.ability.dto.RespMessage;
import com.poll.common.CodeContants;
import com.poll.common.Constants;
import com.poll.common.MsgCode;
import com.poll.common.exception.ApiBizException;
import com.poll.common.util.ApplicationContextUtil;
import com.poll.common.util.AvailableUtil;
import com.poll.common.util.StringUtil;
import com.poll.dao.service.ThirdAccessService;
import com.poll.dao.service.ThirdInterfaceService;
import com.poll.entity.ThirdAccessEntity;
import com.poll.entity.ThirdInterfaceEntity;
import com.poll.entity.ThirdRoleEntity;
import com.poll.entity.ext.ThirdAccessEntityExt;
import com.poll.entity.ext.ThirdRoleInterfaceRltEntityExt;
import com.poll.service.util.TransferUtil;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

/**
 *
 * 接入管理模块
 *
 * 管理所有接入接口，包括h5请求
 *
 * @author gaoyuan
 * @since 2018-07-20
 **/
@Service
public class AccessModuleService {

    protected Logger log = LogManager.getLogger();

    @Value("${accessModule.defaultCode}")
    @Getter
    private String defaultCode;

    @Autowired
    private ThirdAccessService thirdAccesService;

    @Autowired
    private ThirdInterfaceService thirdInterfaceService;

    /**
     * 将请求参数字符串解析为ReqMessage对象
     *
     * @param reqParamStr
     * @return
     */
    public ReqMessage parseReqParam(String reqParamStr) throws Exception {

        ReqMessage reqMsg = new ReqMessage(reqParamStr);

        String appId = reqMsg.parseAppIdFromReqJo();
        if (appId == null) {
            appId = defaultCode;
        }

        //取接入配置
        ThirdAccessEntityExt third = thirdAccesService.selectWithAuthInfo(appId);
        if (third == null) {
            throw new ApiBizException(MsgCode.C00000024.code, MsgCode.C00000024.msg);
        }

        //接入状态判断
        if (!Constants.FLAG_YES_BYTE.equals(third.getStatus())) {
            throw new ApiBizException(MsgCode.C00000025.code, MsgCode.C00000025.msg);
        }

        reqMsg.setThird(third);

        Byte devType = third.getDevType();

        //h5请求不需要解密处理
        if (devType.equals(CodeContants.DEV_TYPE.H5.getValue())) {
            return reqMsg;
        }

        //其他接入方法需要解密
        if (devType.equals(CodeContants.DEV_TYPE.ANDROID.getValue()) ||
            devType.equals(CodeContants.DEV_TYPE.IOS.getValue()) ||
            devType.equals(CodeContants.DEV_TYPE.PLATFORM.getValue())) {

            return reqMsg.parse2ReqMsg();
        }

        throw new ApiBizException(MsgCode.C00000023.code, MsgCode.C00000023.msg);
    }

    /**
     * 适配返回
     *
     * 业务执行结束后，根据请求来源类型，封装不同格式消息返回
     *
     * @param reqMsg
     * @param respMsg
     * @return
     * @throws Exception
     */
    public RespMessage adaptRespParam(ReqMessage reqMsg, RespMessage respMsg) throws Exception {

        Byte devType = reqMsg.getThird().getDevType();

        //h5方式不需要加密
        if (devType.equals(CodeContants.DEV_TYPE.H5.getValue())) {
            respMsg.setCvtData2Jo(true);
            return respMsg;
        }

        //其他接入方式需要加密
        if (devType.equals(CodeContants.DEV_TYPE.ANDROID.getValue()) ||
            devType.equals(CodeContants.DEV_TYPE.IOS.getValue()) ||
            devType.equals(CodeContants.DEV_TYPE.PLATFORM.getValue())) {

            return respMsg.cvt2RsaRespMsg(reqMsg);
        }

        throw new ApiBizException(MsgCode.C00000023.code, MsgCode.C00000023.msg);
    }

    /**
     * 反射调用业务逻辑
     *
     * 根据路径参数【serviceName】【version】得到业务对象 根据路径参数【methodName】得到调用方法
     *
     * @param serviceName
     * @param methodName
     * @param requestUri
     * @param version
     * @param reqMsg
     * @return
     * @throws Exception
     */
    public RespMessage invokeBusiness(String serviceName, String methodName, String requestUri, String version, ReqMessage reqMsg, MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws Exception {

        //取得业务执行对象
        Object businessObj = getBusinessObj(serviceName, version);
        if (businessObj == null) {
            return RespMessage.genError(MsgCode.C00000020.code, MsgCode.C00000020.msg);
        }

        Method method = null;
        try {
            if (file == null) {
                method = businessObj.getClass().getMethod(methodName, JSONObject.class, HttpServletRequest.class, HttpServletResponse.class);
            } else {
                method = businessObj.getClass().getMethod(methodName, JSONObject.class, MultipartFile.class, HttpServletRequest.class, HttpServletResponse.class);
            }
        } catch (Exception e) {
            log.error(String.format("invokeBusiness error[service:%s mtd:%s version:%s uri:%s]->%s", serviceName, methodName, version, requestUri, e.getMessage()));
        }
        if (method == null) {
            return RespMessage.genError(MsgCode.C00000020.code, MsgCode.C00000020.msg);
        }

        ThirdAccessEntityExt third = reqMsg.getThird();

        //角色校验
        verifyRole(third.getRole());

        //请求路径校验权限校验
        veriftyInterface(third, serviceName, methodName, version, requestUri);

        //解析请求参数对象
        JSONObject reqJo = reqMsg.getReqJo();

        //放入版本信息
        TransferUtil.setValue2Json(TransferUtil.VERSION_KEY, version, reqJo);

        //反射调用方法
        Object result = null;
        if (file == null) {
            result = method.invoke(businessObj, reqJo, request, response);
        } else {
            result = method.invoke(businessObj, reqJo, file, request, response);
        }
        if (result == null) {
            return null;
        }

        return (RespMessage)result;
    }

    /**
     * 取得业务对象
     * @param serviceName
     * @param version
     * @return
     */
    public static Object getBusinessObj(String serviceName, String version) {
        //取得业务执行对象
        Object businessObj = null;
        try {
            businessObj = ApplicationContextUtil.getBean(serviceName + Constants.STR_UNDERLINE + StringUtil.replaceFirstChar2Upper(version));
        } catch (Exception e) {
        }

        return businessObj;
    }

    /**
     * 取得服务对象名
     * @param obj
     * @return
     */
    public static String constructServiceName(Object obj) {
        String simpleName = obj.getClass().getSimpleName();
        return constructServiceName(simpleName.substring(0, simpleName.length() - 10));
    }
    public static String constructServiceName(String name) {
        return StringUtil.replaceFirstChar2Lower(name + "Service");
    }

    /**
     * 校验接入角色是否有效
     * @param role
     */
    public ThirdRoleEntity verifyRole(ThirdRoleEntity role) throws Exception {

        if (role == null) {
            throw new ApiBizException(MsgCode.C00000024.code, MsgCode.C00000024.msg);
        }

        AvailableUtil.checkStatusAndTime(role.getStatusTr(), role.getStartTimeTr(), role.getEndTimeTr(), true, MsgCode.C00000261.msg);

        return role;
    }

    /**
     * 校验接入角色与方法映射是否有效
     * @param trirExt
     */
    public ThirdRoleInterfaceRltEntityExt verifyRoleInterfaceRlt(ThirdRoleInterfaceRltEntityExt trirExt) throws Exception {

        if (trirExt == null) {
            throw new ApiBizException(MsgCode.C00000022.code, MsgCode.C00000022.msg);
        }

        AvailableUtil.checkStatusAndTime(trirExt.getStatusTrir(), trirExt.getStartTimeTrir(), trirExt.getEndTimeTrir(), true, MsgCode.C00000261.msg);

        return trirExt;
    }

    /**
     * 校验角色明细授权接口版本
     * @param trirExt
     * @param requestVersion
     * @throws Exception
     */
    public void verifyRoleInterfaceRleVersion(ThirdRoleInterfaceRltEntityExt trirExt, String requestVersion) throws Exception {

        requestVersion = requestVersion.toLowerCase();
        String versionTrir = trirExt.getVersionTrir().toLowerCase();

        AvailableUtil.checkStrMatchRule(requestVersion, versionTrir, true, MsgCode.C00000022.msg);

        //*所有版本 -反向指定 逗号分割正向指定
//        if (!Constants.STR_STAR.equals(versionTrir)) {
//            if (versionTrir.startsWith(Constants.STR_MINUS)) {
//                String substring = versionTrir.substring(1);
//                List<String> versionList = Arrays.asList(substring.split(Constants.STR_COMMA));
//                if (versionList.contains(requestVersion)) {
//                    throw new ApiBizException(MsgCode.C00000022.code, MsgCode.C00000022.msg);
//                }
//            } else {
//                List<String> versionList = Arrays.asList(versionTrir.split(Constants.STR_COMMA));
//                if (!versionList.contains(requestVersion)) {
//                    throw new ApiBizException(MsgCode.C00000022.code, MsgCode.C00000022.msg);
//                }
//            }
//        }
    }

    /**
     * 用户请求接口权限校验
     *
     * @param third
     * @param serviceName
     * @param methodName
     * @param version
     * @param requestUri
     * @throws Exception
     */
    public void veriftyInterface(ThirdAccessEntityExt third, String serviceName, String methodName, String version, String requestUri) throws Exception {

        //目标接口
        ThirdInterfaceEntity ti = null;

        //正则授权
        if (CodeContants.THIRD_ACCESS_AUTH_TYPE.REGULAR_AUTH.getValue().equals(third.getRole().getAuthTypeTr())) {

            AvailableUtil.checkStrMatchReg(requestUri, third.getRole().getAuthStrTr(), true, MsgCode.C00000022.msg);

            ti = thirdInterfaceService.selectByServiceMethodNameCache(serviceName, methodName);

        } else {//明细授权

            Map<String, Map<String, ThirdRoleInterfaceRltEntityExt>> roleInterfaceRltMap = third.getRoleInterfaceRltMap();
            if (roleInterfaceRltMap == null) {
                log.info(String.format("veriftyInterface failture[service:%s mtd:%s version:%s uri:%s]->detail auth failture: no role interface relation mapping in db", serviceName, methodName, version, requestUri));
                throw new ApiBizException(MsgCode.C00000022.code, MsgCode.C00000022.msg);
            }

            Map<String, ThirdRoleInterfaceRltEntityExt> methodMap = roleInterfaceRltMap.get(serviceName);
            if (methodMap == null) {
                log.info(String.format("veriftyInterface failture[service:%s mtd:%s version:%s uri:%s]->detail auth failture: no method record under role", serviceName, methodName, version, requestUri));
                throw new ApiBizException(MsgCode.C00000022.code, MsgCode.C00000022.msg);
            }

            //校验角色接口映射有效性
            ThirdRoleInterfaceRltEntityExt trirExt = verifyRoleInterfaceRlt(methodMap.get(methodName));

            //校验请求版本
            verifyRoleInterfaceRleVersion(trirExt, version);

            ti = trirExt.getThirdInterface();
        }

        //校验接口是否可用
        if (ti == null) {
            log.info(String.format("veriftyInterface failture[service:%s mtd:%s version:%s uri:%s]->detail auth failture: no method record under role", serviceName, methodName, version, requestUri));
            throw new ApiBizException(MsgCode.C00000020.code, MsgCode.C00000020.msg);
        }

        AvailableUtil.checkStatusAndTime(ti.getStatusTi(), ti.getStartTimeTi(), ti.getEndTimeTi(), true, MsgCode.C00000262.msg);
    }

    /**
     * 根据code查询得到接入管理对象，若接入不可用，则返回null
     * @param code
     * @return
     */
    public ThirdAccessEntity getThirdByCode(String code) throws ApiBizException {

        ThirdAccessEntity third = thirdAccesService.selectByIdCache(code);
        if (third == null || !Constants.FLAG_YES_BYTE.equals(third.getStatus())) {
            throw new ApiBizException(MsgCode.C00000025.code, MsgCode.C00000025.msg);
        }
        return third;
    }


    public static void main(String[] args) {
        String s = "/poll/frt/v1/test/mtd";
        String regular = ".*?/v1/\\w+/\\w+";
        System.out.println(s.matches(regular));
    }

}
