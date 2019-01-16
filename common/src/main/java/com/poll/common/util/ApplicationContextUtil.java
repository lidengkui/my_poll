package com.poll.common.util;

import com.poll.common.Constants;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

public class ApplicationContextUtil {

    public static ApplicationContext applicationContext;

    public static String domain;
    public static String domainWithContext;
    public static String previewPrefix;

    public static Object getBean(String name) throws BeansException {
        name = StringUtil.trimStr(name);
        if (name.equals(Constants.STR_BLANK) || applicationContext == null) {
            return null;
        }

        try {
            return applicationContext.getBean(name);
        } catch (BeansException e) {
        }
        return null;
    }
}
