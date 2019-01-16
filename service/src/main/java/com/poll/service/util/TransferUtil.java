package com.poll.service.util;

import com.alibaba.fastjson.JSONObject;
import com.poll.entity.UserEntity;

import java.util.Date;

/**
 * @description 用户处理相关util
 **/
public class TransferUtil {

    public static final String USER_INFO_KEY = "__userInfo";
    public static final String ACTIVITY_CODE_KEY = "__actvtCode";
    public static final String ACTIVITY_INFO_KEY = "__actvtInfo";
    public static final String DEV_TYPE_KEY = "__devType";
    public static final String VERSION_KEY = "__version";
    public static final String DATE_KEY = "__date";
    public static final String RESOURCE_CODE_KEY = "__resourceCode";
    public static final String PRE_CHECK_NAME_KEY = "__preCheckName";
    public static final String JO_KEY = "__jo";

    public static JSONObject setValue2Json(String key, Object value, JSONObject jo) {
        if (jo != null) {
            jo.put(key, value);
        }
        return jo;
    }

    public static <T>T parseValueFromJson(JSONObject jo, String key, Class<T> cls) {
        if (jo != null) {
            return jo.getObject(key, cls);
        }
        return null;
    }

    public static JSONObject setDate2Json(Date sysDate, JSONObject jo) {
        if (jo != null) {
            jo.put(DATE_KEY, sysDate);
        }
        return jo;
    }
    public static Date parseDateFromJson(JSONObject jo) {
        Date sysDate = null;
        if (jo != null) {
            sysDate = jo.getObject(DATE_KEY, Date.class);
        }
        return sysDate;
    }

    /**
     * 向目标json对象中，放入user对象信息
     * @param user
     * @param jo
     * @return
     */
    public static JSONObject setUserInfo2Json(UserEntity user, JSONObject jo) {
        if (jo != null) {
            //放入用户信息
            jo.put(USER_INFO_KEY, user);
        }
        return jo;
    }

    /**
     * 从目标json对象中，解析出用户信息
     * @param jo
     * @return
     */
    public static UserEntity parseUserInfoFromJson(JSONObject jo) {
        UserEntity user = null;
        if (jo != null) {
            user = jo.getObject(USER_INFO_KEY, UserEntity.class);
        }
        return user;
    }

    /**
     * 向目标json对象中，放入活动编码信息
     * @param activityCode
     * @param jo
     * @return
     */
    public static JSONObject setActivityCode2Json(String activityCode, JSONObject jo) {
        if (jo != null) {
            //放入活动编码
            jo.put(ACTIVITY_CODE_KEY, activityCode);
        }
        return jo;
    }
    /**
     * 从目标json对象中，解析出用户信息
     * @param jo
     * @return
     */
    public static String parseActivityCodeFromJson(JSONObject jo) {
        String activityCode = null;
        if (jo != null) {
            activityCode = jo.getString(ACTIVITY_CODE_KEY);
        }
        return activityCode;
    }

    /**
     * 向目标json对象中，放入接入方设备类型信息
     * @param devType
     * @param jo
     * @return
     */
    public static JSONObject setDevType2Json(Byte devType, JSONObject jo) {
        if (jo != null) {
            //放入活动编码
            jo.put(DEV_TYPE_KEY, devType);
        }
        return jo;
    }
    /**
     * 从目标json对象中，解析出设备类型
     * @param jo
     * @return
     */
    public static Byte parseDevTypeFromJson(JSONObject jo) {
        Byte devType = null;
        if (jo != null) {
            devType = jo.getByte(DEV_TYPE_KEY);
        }
        return devType;
    }

}
