package com.poll.common;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Constants {

    public static final String STR_BLANK = "";
    public static final String STR_DOT = ".";
    public static final String STR_COMMA = ",";
    public static final String STR_SEMICOLON = ";";
    public static final String STR_EQUAL = "=";
    public static final String STR_QUESTION_MARK = "?";
    public static final String STR_AMPERSAND = "&";
    public static final String STR_COLON = ":";
    public static final String STR_MINUS = "-";
    public static final String STR_UNDERLINE = "_";
    public static final String STR_STAR = "*";
    public static final String STR_CRLF = "\r\n";
    public static final String FILE_FMT_XLSX = ".xlsx";
    public static final String FILE_EXTENSION_XLS = "xls";
    public static final String CHARSET_UTF8 = "utf-8";
    public static final String PREFIX_HTTP = "http://";
    public static final String PREFIX_HTTPS = "https://";
    //加密算法
    public static final String ENC_NAME_AES = "AES";
    public static final String ENC_NAME_DES = "DES";
    public static final String ENC_NAME_3DES = "DESede";
    public static final String ENC_NAME_RSA = "RSA";
    //ECB/CBC/PCBC/CTR/CTS/CFB/CFB8 to CFB128/OFB/OBF8 to OFB128
    public static final String ENC_MODEL_ECB = "ECB";
    public static final String ENC_MODEL_CBC = "CBC";
    public static final String ENC_MODEL_PCBC = "PCBC";
    public static final String ENC_MODEL_CRT = "CRT";
    public static final String ENC_MODEL_CTS = "CTS";
    public static final String ENC_MODEL_CFB = "CFB";
    public static final String ENC_MODEL_OFB = "OFB";

    public static final String ENC_FILL_NOPADDING = "NoPadding";
    public static final String ENC_FILL_PKCS5PADDING = "PKCS5Padding";
    public static final String ENC_FILL_PKCS1PADDING = "PKCS1Padding";
    public static final String ENC_FILL_ISO10126PADDING = "ISO10126Padding";

    public static final Byte BYTE_NEGATIVE = -1;
    public static final Byte BYTE0 = 0;
    public static final Byte BYTE1 = 1;
    public static final Byte BYTE2 = 2;
    public static final Byte BYTE3 = 3;
    /**
     * 是、否标记
     */
    public static final char FLAG_YES_CHAR = '1';
    public static final char FLAG_NO_CHAR = '0';
    public static final Byte FLAG_YES_BYTE = BYTE1;

    //毫秒数
    public static final long MILLS_MINUTE30 = 1800000L;
    public static final long MILLS_DAY1 = 86400000L;
    public static final long MILLS_DAY2 = 172800000L;

    //redis中的key前缀
    public static final String REDIS_KEY_PREFIX = "poll:";
    //频繁检查
    public static final String REDIS_KEY_PREFIX_FREQT = REDIS_KEY_PREFIX + "freqt:";
    public static final String REDIS_KEY_PREFIX_TOKEN = REDIS_KEY_PREFIX + "token:";
    //图形验证码前缀
    public static final String REDIS_KEY_PREFIX_KAPTCHA = REDIS_KEY_PREFIX + "kaptcha:";
    //用户账号前缀
    public static final String REDIS_KEY_PREFIX_USER = REDIS_KEY_PREFIX + "user:";

    public static final String POLL_ORDER_COUNT_KEY = REDIS_KEY_PREFIX + "pollCodeCount:";


    public static final Map poll_COLUMN_MAP = new HashMap<Integer, String>();
    static {
        //初始化excel键值对从A-AZ列
        int index = -1;
        boolean isPassed = false;
        do {
            for (int i = 65; i <= 90; i++) {
                index++;
                poll_COLUMN_MAP.put(index, (isPassed ? (char)65 :"") + "" + (char) i);
            }
            isPassed = isPassed ? false : true;
        } while (isPassed);
    }
}
