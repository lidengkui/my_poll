package com.poll.service.module.urlComplter;

import com.alibaba.fastjson.JSONObject;
import com.poll.common.Constants;
import com.poll.common.util.ApplicationContextUtil;
import com.poll.common.util.StringUtil;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;


@Service
public class CompltDomainUrlComplterService implements UrlComplterService {

    @Override
    public String complt(String url, String prefix, JSONObject reqJo, HttpServletRequest request) {

        if (url != null) {
            if (url.startsWith(Constants.PREFIX_HTTP) || url.startsWith(Constants.PREFIX_HTTPS)) {
                return url;
            }

            return ApplicationContextUtil.domainWithContext + StringUtil.trimStr(prefix) + url;
        }

        return url;
    }
}
