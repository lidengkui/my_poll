package com.poll.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class CodeContants {

    /**
     * 周期规则限制类型
     *
     */
    @AllArgsConstructor
    public static enum RESOURCE_TYPE {

        T("T", "总量"),
        H("H", "每小时"),
        D("D", "每天"),
        W("W", "每周"),
        M("M", "每月"),
        ;

        @Getter
        private String value;
        @Getter
        private String name;

        public static RESOURCE_TYPE getTypeByValue(String value) {
            return RESOURCE_TYPE.valueOf(value.toUpperCase());
        }
    }

    /**
     * 周期可用标记类型
     *
     */
    @AllArgsConstructor
    public static enum CYCLE_AVAILABLE_TYPE {

        HOD("HOD", "日中时"),
        DOW("DOW", "周中日"),
        DOM("DOM", "月中日"),
        DOY("DOY", "年中日"),
        WOM("WOM", "月中周"),
        WOY("WOY", "年中周"),
        MOY("MOY", "年中月"),
        ;

        @Getter
        private String value;
        @Getter
        private String name;
    }

    /**
     * 设备类型
     */
    @AllArgsConstructor
    public static enum DEV_TYPE {

        H5(Constants.BYTE0, "H5"),
        ANDROID(Constants.BYTE1, "android"),
        IOS(Constants.BYTE2, "ios"),
        PLATFORM(Constants.BYTE3, "第三方平台"),
        ;

        @Getter
        private Byte value;
        @Getter
        private String name;
        public static DEV_TYPE findTravellerByName(String name) {
            for (DEV_TYPE value:DEV_TYPE.values()) {
                if (value.getName().equals(name)){
                    return value;
                }
            }
            return PLATFORM;
        }
    }

    /**
     * 第三方接入授权类型
     */
    @AllArgsConstructor
    public static enum THIRD_ACCESS_AUTH_TYPE {

        REGULAR_AUTH(Constants.BYTE0, "正则授权"),
        DETAIL_AUTH(Constants.BYTE1, "明细授权"),
        ;

        @Getter
        private Byte value;
        @Getter
        private String name;
    }




}
