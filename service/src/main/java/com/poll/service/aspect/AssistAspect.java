package com.poll.service.aspect;

import com.alibaba.fastjson.JSONObject;
import com.poll.common.MsgCode;
import com.poll.common.exception.ApiBizException;
import com.poll.common.util.CheckUtil;
import com.poll.common.util.DateUtil;
import com.poll.dao.service.SysConfigService;
import com.poll.entity.SysConfigEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;


@Aspect
@Order(2)
@Component
public class AssistAspect {

    protected Logger log = LogManager.getLogger();

    @Autowired
    protected SysConfigService sysConfigService;

    @Value("${assistConf.pwdKeyInDb}")
    protected String pwdKeyInDb;

    @Pointcut(value = "execution(com.poll.ability.dto.RespMessage com.poll.service.business.AssistService*.*(..)) && args(reqJo, request, response)")
    public void cut(JSONObject reqJo, HttpServletRequest request, HttpServletResponse response){
    }

    /**
     * 调用业务方法前，检查token， StartUpService下的所有方法不检查
     * @param reqJo
     * @param request
     * @param response
     * @throws Exception
     */
    @Before(value = "cut(reqJo, request, response)")
    public void checkAssist(JSONObject reqJo, HttpServletRequest request, HttpServletResponse response) throws Exception {

        log.debug("before assist interface aspect working-->" + request.getRequestURI());

        String pwd = CheckUtil.checkParamSimpleFromTo("pwd", "授权参数", String.class, request, null, false, null, 1, 50, null);

        //密码检查
        SysConfigEntity config = sysConfigService.selectById(pwdKeyInDb);
        if (config == null) {
            throw new ApiBizException(MsgCode.C00000023.code, MsgCode.C00000023.msg);
        }
        if (!pwd.equals(config.getConfigValue1())) {
            throw new ApiBizException(MsgCode.C00000040.code, MsgCode.C00000040.msg);
        }
        String expireDataStr = config.getConfigValue2();
        if (expireDataStr == null) {
            throw new ApiBizException(MsgCode.C00000023.code, MsgCode.C00000023.msg);
        }

        Date sysDate = new Date();

        //检查过期
        Date expireDate = null;
        try {
            expireDate = DateUtil.convertStr2Date(expireDataStr);
        } catch (Exception e) {
        }
        if (expireDate == null || sysDate.compareTo(expireDate) >= 0) {
            throw new ApiBizException(MsgCode.C00000023.code, MsgCode.C00000023.msg);
        }
    }
}
