package com.poll.ability.util;

import com.poll.common.util.HttpUtil;

import java.util.HashMap;
import java.util.Map;


public class HttpPostUtil {

    private static Map<String, String> headerParamMap = new HashMap<String, String>();
    static {
        headerParamMap.put(HttpUtil.HEADER_NAME_CONTENT_TYPE, "application/json");
        headerParamMap.put(HttpUtil.HEADER_NAME_ACCEPT, "application/json");
    }

}
