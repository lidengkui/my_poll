package com.poll.service.aspect;

import com.alibaba.fastjson.JSONObject;
import com.poll.common.ConstantsOfParamName;
import com.poll.common.MsgCode;
import com.poll.common.exception.ApiBizException;
import com.poll.common.util.DateUtil;
import com.poll.entity.UserEntity;
import com.poll.service.annotation.TokenLess;
import com.poll.service.module.TokenModuleService;
import com.poll.service.util.TransferUtil;
import io.jsonwebtoken.impl.DefaultClaims;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;


@Aspect
@Order(0)
@Component
public class TokenAspect {

    protected Logger log = LogManager.getLogger();

    @Autowired
    private TokenModuleService tokenModuleService;

    @Pointcut(value = "execution(com.poll.ability.dto.RespMessage com.poll.service.business..*.*(..)) && args(reqJo, request, response)")
    public void cutNoFile(JSONObject reqJo, HttpServletRequest request, HttpServletResponse response){
    }
    @Pointcut(value = "execution(com.poll.ability.dto.RespMessage com.poll.service.business..*.*(..)) && args(reqJo, file, request, response)")
    public void cutFile(JSONObject reqJo, MultipartFile file, HttpServletRequest request, HttpServletResponse response){
    }

    /**
     * 调用业务方法前，检查token， StartUpService下的所有方法不检查
     * @param reqJo
     * @param request
     * @param response
     * @throws Exception
     */
    @Before(value = "cutNoFile(reqJo, request, response) && !@annotation(com.poll.service.annotation.TokenLess)")
    public void checkTokenNofile1(JSONObject reqJo, HttpServletRequest request, HttpServletResponse response) throws Exception {

        log.debug("before No TokenLess Annotation aspect working-->" + request.getRequestURI());

        //校验token并拿到user对象
//        String token = request.getHeader(ConstantsOfParamName.POLL_TOKEN);
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (ConstantsOfParamName.POLL_TOKEN.equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token == null) {
            throw new ApiBizException(MsgCode.C00000021.code, MsgCode.C00000021.msg);
        }

        //解析内容
        DefaultClaims claims = null;
        try {
            claims = tokenModuleService.checkAccessToken(token, tokenModuleService.getSubjectUser(), true);
        } catch (Exception e) {
            //若过期，则刷新一个新token放置于response中
            if (e instanceof ApiBizException) {
                ApiBizException ape = (ApiBizException) e;

                //只是28码才认定为过期，其他不处理
                if (MsgCode.C00000028.code.equals(ape.getErrorCode())) {
                    claims = (DefaultClaims)ape.getObject();
                    token = tokenModuleService.refreshClaims4User(claims);

                    Cookie cookie = new Cookie(ConstantsOfParamName.POLL_TOKEN, token);
                    cookie.setPath("/");
                    cookie.setMaxAge(-1);
                    response.addCookie(cookie);

//                    response.setHeader(ConstantsOfParamName.POLL_TOKEN, token);
                }
            } else {
                throw e;
            }
        }

        //放入用户信息
        TransferUtil.setUserInfo2Json(tokenModuleService.checkClaims4User(claims), reqJo);
    }

    public static void main(String[] args) {
        System.out.println(DateUtil.convertDate2Str(new Date(1540973970301L), DateUtil.FORMATE_YYYYMMDDHHMMSSSSS));
    }

    @Before(value = "cutFile(reqJo, file, request, response) && !@annotation(com.poll.service.annotation.TokenLess)")
    public void checkTokenFile1(JSONObject reqJo, MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws Exception {
        checkTokenNofile1(reqJo, request, response);
    }

    /**
     * 调用业务方法前，检查token， StartUpService下的所有方法不检查
     * @param reqJo
     * @param request
     * @param response
     * @throws Exception
     */
    @Before(value = "cutNoFile(reqJo, request, response) && @annotation(tokenLess)")
    public void checkTokenNofile2(JSONObject reqJo, HttpServletRequest request, HttpServletResponse response, TokenLess tokenLess) throws Exception {

        log.debug("before TokenLess Annotation aspect working-->" + request.getRequestURI());

        if (tokenLess.tryParseUser()) {
            try {
                //校验token并拿到user对象
                String token = null;
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if (ConstantsOfParamName.POLL_TOKEN.equals(cookie.getName())) {
                            token = cookie.getValue();
                            break;
                        }
                    }
                }

                if (token != null) {
                    UserEntity user = tokenModuleService.checkClaims4User(token);

                    //放入用户信息
                    TransferUtil.setUserInfo2Json(user, reqJo);
                }
            } catch (Exception e) {
            }
        }
    }

    @Before(value = "cutFile(reqJo, file, request, response) && @annotation(tokenLess)")
    public void checkTokenFile2(JSONObject reqJo, MultipartFile file, HttpServletRequest request, HttpServletResponse response, TokenLess tokenLess) throws Exception {
        checkTokenNofile2(reqJo, request, response, tokenLess);
    }

}
