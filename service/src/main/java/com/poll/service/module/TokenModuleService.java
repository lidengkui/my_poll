package com.poll.service.module;

import com.poll.common.CodeContants;
import com.poll.common.Constants;
import com.poll.common.MsgCode;
import com.poll.common.exception.ApiBizException;
import com.poll.common.util.AESUtil;
import com.poll.common.util.StringUtil;
import com.poll.dao.service.UserService;
import com.poll.entity.UserEntity;
import com.poll.entity.ext.UserEntityExt;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenModuleService {

    @Autowired
    private UserService userService;

    //通用header
    public Map<String, Object> baseJwtHeaderMap = new HashMap<String, Object>();
    {
        baseJwtHeaderMap.put("alg", "HS256");
        baseJwtHeaderMap.put("typ", "JWT");
    }

    @Value("${jwtConf.signKey}.getBytes()")
    @Getter
    private byte[] jwtSignKeyBytes;

    @Value("${jwtConf.encKey}")
    @Getter
    private String encKey;

    @Value("${jwtConf.issuer}")
    @Getter
    private String issuer;

    @Value("${jwtConf.tokenTypeMap.user.subject}")
    @Getter
    private String subjectUser;

    @Value("${jwtConf.tokenTypeMap.user.durationMills}")
    @Getter
    private Long durationUser;

    @Value("${jwtConf.tokenTypeMap.user.bindPhoneTokenMills}")
    @Getter
    private Long bindPhoneTokenMills;

    @Value("${jwtConf.tokenTypeMap.user.loginMills}")
    @Getter
    private Long loginMills;

    public static final String STR_EXP_AT = "expAt";                            //token过期时间
    public static final String STR_TOKEN_FLAG = "tokenFlag";             //登录密码更新标记，用于修改登录密码后token过期
    public static final String STR_USER_ID = "userId";
    public static final String STR_COMPANY_ID = "companyId";
    public static final String STR_MOBILE = "mobile";
    public static final String STR_LOGIN_PASSWORD = "loginPassword";
    public static final String STR_MOBILE_ENC = "mobileEnc";

    //敏感内容加密工具
    private AESUtil aes4Jwt;
    @PostConstruct
    private void getAes() {
        aes4Jwt = AESUtil.getInstance(encKey);
    }

    /**
     * 生成用户认证jwt
     * @param userId
     * @param mobile
     * @param tokenFlag   用户登录密码随机串，用于用户密码修改时强制用户重新登录
     * @param deviceIdentfCode  用户设备识别码，用于用户更换设备后强制重新登录与单用户在线
     * @param claimsMap         声明信息 可为空
     * @return
     * @throws Exception
     */
    public String genAccessToken4User(Long userId, Long companyId, Long mobile, String tokenFlag, String deviceIdentfCode, Map<String, Object> claimsMap) throws Exception {

        if (claimsMap == null) {
            claimsMap = new HashMap<String, Object>();
        }
        claimsMap.put(STR_USER_ID, aes4Jwt.encrypt(userId.toString()));
        claimsMap.put(STR_TOKEN_FLAG, tokenFlag);

        if (companyId != null) {
            claimsMap.put(STR_COMPANY_ID, aes4Jwt.encrypt(companyId.toString()));
        }
        if (mobile != null) {
            String mobileStr = mobile.toString();
            claimsMap.put(STR_MOBILE_ENC, aes4Jwt.encrypt(mobileStr));
            claimsMap.put(STR_MOBILE, StringUtil.replaceCharAuto(mobileStr));
        }

        Date expireAt = new Date(System.currentTimeMillis() + durationUser);

        return createJwtExpirationAt(claimsMap, subjectUser, expireAt);
    }

    /**
     * 将token解析为user对象
     * @param jwtStr
     * @return
     * @throws Exception
     */
    public UserEntityExt checkClaims4User(String jwtStr) throws Exception {

        DefaultClaims claims = checkAccessToken(jwtStr, subjectUser, true);

        return checkClaims4User(claims);
    }
    public UserEntityExt checkClaims4User(DefaultClaims claims) throws Exception {

        UserEntityExt user = new UserEntityExt();

        //若加解密失败,则强制重新登录
        try {
            user.setId(Long.parseLong(aes4Jwt.decrypt(claims.get(STR_USER_ID).toString())));
            user.setCompanyId(Long.parseLong(aes4Jwt.decrypt(claims.get(STR_COMPANY_ID).toString())));

            Object mobileObj = claims.get(STR_MOBILE_ENC);
            if (mobileObj != null) {
                user.setMobile(Long.parseLong(aes4Jwt.decrypt(mobileObj.toString())));
            }
        } catch (Exception e) {
            throw new ApiBizException(MsgCode.C00000021.code, MsgCode.C00000021.msg);
        }

        UserEntityExt userCertain = userService.getByIdCache(user.getId());
        if (userCertain == null) {
            throw new ApiBizException(MsgCode.C00000021.code, MsgCode.C00000021.msg);
        }
        //校验用户状态
        if (!Constants.BYTE1.equals(userCertain.getStatus())) {
            throw new ApiBizException(MsgCode.C00000026.code, MsgCode.C00000026.msg);
        }

        //校验密码是否修改
        String tokenFlag = StringUtil.trimStr(claims.get(STR_TOKEN_FLAG));
        if (!tokenFlag.equals(userCertain.getTokenFlag())) {
            throw new ApiBizException(MsgCode.C00000021.code, MsgCode.C00000021.msg);
        }
        user.setTokenFlag(tokenFlag);

        //补充所属企业信息
        user.setCompany(userCertain.getCompany());

        return user;
    }

    /**
     * 解析claims
     * @param jwtStr
     * @return
     * @throws ApiBizException
     */
    private DefaultClaims parseClaims(String jwtStr) throws ApiBizException {
        try {
            Jwt parse = Jwts.parser().setSigningKey(jwtSignKeyBytes).parse(jwtStr);
            return (DefaultClaims) parse.getBody();
        } catch (ExpiredJwtException e1) {
            throw new ApiBizException(MsgCode.C00000028.code, MsgCode.C00000028.msg);
        } catch (Exception e2) {
            throw new ApiBizException(MsgCode.C00000021.code, MsgCode.C00000021.msg);
        }
    }

    /**
     * 验证jwt
     * @param jwtStr
     * @param subject
     * @return
     * @throws Exception
     */
    public DefaultClaims checkAccessToken(String jwtStr, String subject, boolean checkDate) throws Exception {

        if (jwtStr == null) {
            throw new ApiBizException(MsgCode.C00000021.code, MsgCode.C00000021.msg);
        }

        DefaultClaims claims = parseClaims(jwtStr);

        //校验发布者
        if (!issuer.equals(claims.getIssuer())) {
            throw new ApiBizException(MsgCode.C00000021.code, MsgCode.C00000021.msg);
        }

        //校验主题
        if (!subject.equals(claims.getSubject())) {
            throw new ApiBizException(MsgCode.C00000021.code, MsgCode.C00000021.msg);
        }

        if (checkDate) {
            //校验过期时间
            Date expiration = new Date((Long)claims.get(STR_EXP_AT));
            if (expiration != null) {
                Date sysDate = new Date();
                if (sysDate.getTime() - expiration.getTime() >= loginMills) {
                    throw new ApiBizException(MsgCode.C00000021.code, MsgCode.C00000021.msg);
                }
                if (sysDate.compareTo(expiration) >= 0) {
                    throw new ApiBizException(MsgCode.C00000028.code, MsgCode.C00000028.msg, claims);
                }
            }
        }

        return claims;
    }

    /**
     * 刷新token
     * @param jwtStr
     * @return
     * @throws Exception
     */
    public String refreshClaims4User(String jwtStr) throws Exception {

        return refreshClaims4User(checkAccessToken(jwtStr, subjectUser, false));
    }

    public String refreshClaims4User(DefaultClaims claims) throws Exception {

        return createJwtExpirationAt(claims, subjectUser, new Date(System.currentTimeMillis() + durationUser));
    }

    /**
     * 生成指定过期时间的token
     * @param claims
     * @param subject
     * @param expirationAt
     * @return
     */
    public String createJwtExpirationAt(Map<String, Object> claims, String subject, Date expirationAt) {

        JwtBuilder jwt = Jwts.builder()
                            .setHeader(baseJwtHeaderMap)
                            .setClaims(claims)
                            .setSubject(subject)
                            .setIssuer(issuer)
                            .setIssuedAt(new Date())
                            .signWith(SignatureAlgorithm.HS256, jwtSignKeyBytes);

        if (expirationAt != null) {
//            jwt.setExpiration(expirationAt);
            claims.put("expAt", expirationAt);
        }

       return jwt.compact();
    }

    /**
     * 生成修改手机号登录密码校验令牌
     * @param pwd
     * @return
     * @throws Exception
     */
    public String genStrPwdLoginToken(Long userId ,String pwd) throws  Exception{
        Map<String ,Object> genAccessMap = new HashMap<>();
        genAccessMap.put(STR_USER_ID,userId);
        genAccessMap.put(STR_LOGIN_PASSWORD,pwd);
        Date expireAt = new Date(System.currentTimeMillis() +bindPhoneTokenMills );
        return  createJwtExpirationAt(genAccessMap, subjectUser, expireAt);
    }
}
