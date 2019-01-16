package com.poll.service.module;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.poll.common.Constants;
import com.poll.common.MsgCode;
import com.poll.common.exception.ApiBizException;
import com.poll.common.util.DateUtil;
import com.poll.common.util.MD5Util;
import com.poll.common.util.RandomUtil;
import com.poll.dao.service.UserService;
import com.poll.entity.UserEntity;
import com.poll.redis.RedisService;
import com.poll.service.conf.KaptchaConfig;
import com.poll.service.module.urlComplter.UrlComplterService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;


@Service
public class UserModuleService {
    protected Logger log = LogManager.getLogger();

    @Autowired
    protected DefaultKaptcha defaultKaptcha;

    @Autowired
    protected KaptchaConfig kaptchaConfig;

    @Autowired
    protected UrlComplterService urlComplterService;

    @Autowired
    protected RedisService redisService;

    @Autowired
    protected UserService userService;

    public void genKaptcha(HttpServletResponse response, String serKey) throws IOException {

        response.setDateHeader("Expires", 0);// 禁止server端缓存
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setContentType("image/jpeg");
        response.setHeader("pragma", "no-cache");
        //生成验证码
        String code = defaultKaptcha.createText();
        //放入redis
        redisService.setValue(Constants.REDIS_KEY_PREFIX_KAPTCHA + serKey, code, kaptchaConfig.getDuration() * 60 * 1000);
        //创建图片
        BufferedImage bufferedImage = defaultKaptcha.createImage(code);
        OutputStream out = null;

        out = response.getOutputStream();
        ImageIO.write(bufferedImage, "jpg", out);
        out.flush();
        out.close();

    }

    /**
     * 登录校验
     *
     * @param account
     * @param password
     * @param veriCode
     * @param serializeStr
     * @return
     */
    public UserEntity loginValid(String account, String password, String veriCode, String serializeStr) throws Exception {
        //判断账号是否锁定
        //锁定用户key格式 账号:lock
        String currentUserLockKey = Constants.REDIS_KEY_PREFIX_USER + account + Constants.STR_COLON + "login" + Constants.STR_COLON + "lock";
        if (redisService.hasKey(currentUserLockKey)) {
            log.info("账号【" + account + "】已锁定");
            long expire = redisService.expire(currentUserLockKey);
            String localCh = DateUtil.formatMillisecond(expire * 1000, "$D$h$m");
            throw new ApiBizException(MsgCode.C00000080.code, "账号已锁定，请在" + localCh + "后重试");
        }
        //校验验证码是否匹配
        String redisVeriCode = redisService.getValue(Constants.REDIS_KEY_PREFIX_KAPTCHA + serializeStr);
        if (null != redisVeriCode && veriCode.equalsIgnoreCase(redisVeriCode)) {
            //删除验证码
            redisService.delKey(Constants.REDIS_KEY_PREFIX_KAPTCHA + serializeStr);

            UserEntity currentUser = userService.selectOne(new EntityWrapper<UserEntity>().eq("user_name_u", account).eq("status_u", 1));
            if (null != currentUser) {
                //加盐校验
                String salt = currentUser.getSalt();
                if (null == salt) {
                    salt = Constants.STR_BLANK;
                }
                String passwordWithSalt = handlePasswordWithSalt(password, salt);
                if (!currentUser.getPassword().equals(passwordWithSalt.toLowerCase())) {
                    currentUser = null;
                }
            }
            String currentUserCheckFailCountKey = Constants.REDIS_KEY_PREFIX_USER + account + Constants.STR_COLON + "login" + Constants.STR_COLON + "fail" + Constants.STR_COLON + "count";
            if (null == currentUser) {
                //失败次数key格式  账号:login:fail:count
                long expr = System.currentTimeMillis() + kaptchaConfig.getFailMonitorExpiry() * 60 * 1000;
                long failCount = redisService.count(currentUserCheckFailCountKey, 1, -1, new Date(expr), RedisService.ExpireAtType.NULL, null);
                if (kaptchaConfig.getMaxFailTime() <= failCount) {
                    log.error("超过最大重试次数，账号锁定【" + account + "】");
                    redisService.setValue(currentUserLockKey, Constants.STR_BLANK, kaptchaConfig.getFailLockExpiry() * 60 * 1000);
                    //移除统计失败次数key
                    redisService.delKey(currentUserCheckFailCountKey);
                }
                throw new ApiBizException(MsgCode.C00000002.code, MsgCode.C00000002.msg);
            }
            //重置tokenFlag
            currentUser.setTokenFlag(RandomUtil.genLetterLowerStr(6));
            currentUser.setUpdateTime(new Date());
            try {
                userService.updateById(currentUser);
            } catch (Exception e) {
                throw new ApiBizException(MsgCode.C00000999.code, MsgCode.C00000999.msg);
            } finally {
                //成功登录需重新计算错误次数
                redisService.delKey(currentUserCheckFailCountKey);
            }
            userService.clearCache(currentUser.getId());
            return currentUser;
        }
        //验证码失效造成的登录失败不计入失败次数限制
        log.error("验证码失效，传入验证码【" + veriCode + "】，有效验证码" + redisVeriCode == null ? "过期" : "【" + redisVeriCode + "】");
        throw new ApiBizException(MsgCode.C00000003.code, MsgCode.C00000003.msg);
    }

    /**
     * 加盐加密
     *
     * @param password
     * @param salt
     * @return
     */
    public static String handlePasswordWithSalt(String password, String salt) {
        if (password != null && salt != null) {
            password = MD5Util.encode(password + salt);
        }
        return password;
    }

    /**
     * @param oldPwd 原秘密
     * @param pwd    新密码
     * @return
     */
    public void updatePwdByOldPwd(Long userId, String oldPwd, String pwd) throws Exception {
        UserEntity currentUser = userService.selectOne(new EntityWrapper<UserEntity>().eq("id_u", userId)
                .eq("status", 1));
        if (null != currentUser) {
            //设置新密码
            String salt = currentUser.getSalt();
            if (null == salt) {
                salt = Constants.STR_BLANK;
            }

            String oldPasswordWithSalt = handlePasswordWithSalt(oldPwd, salt);
            if (currentUser.getPassword().equals(oldPasswordWithSalt.toLowerCase())) {
                salt = RandomUtil.genLetterNumStr(4);
                currentUser.setSalt(salt);
                currentUser.setPassword(handlePasswordWithSalt(pwd, salt).toLowerCase());
                currentUser.setTokenFlag(RandomUtil.genLetterNumStr(6));
                currentUser.setUpdateTime(new Date());
                try {
                    userService.updateById(currentUser);
                } catch (Exception e) {
                    throw new ApiBizException(MsgCode.C00000999.code, MsgCode.C00000999.msg);
                }
                userService.clearCache(currentUser.getId());
                return;
            } else {
                throw new ApiBizException(MsgCode.C00000001.code, "原秘密不正确，请检查后重试。");
            }
        }
        throw new ApiBizException(MsgCode.C00000004.code, MsgCode.C00000004.msg);
    }


}


