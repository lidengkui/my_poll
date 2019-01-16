package com.poll.service.module.urlComplter;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;


public interface UrlComplterService {

    /**
     * 为传入url补全系统域名
     * @param url
     * @param prefix
     * @param reqJo
     * @param request
     * @return
     */
    String complt(String url, String prefix, JSONObject reqJo, HttpServletRequest request);
}
