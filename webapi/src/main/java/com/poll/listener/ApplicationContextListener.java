package com.poll.listener;

import com.poll.common.util.ApplicationContextUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @description 容器初始化完成后保存applicationContext引用
 **/
@Configuration
public class ApplicationContextListener implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${sysConf.domain}")
    private String domain;

    @Value("${sysConf.domainWithContext}")
    private String domainWithContext;

    @Value("${sysConf.previewPrefix}")
    private String previewPrefix;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        ApplicationContextUtil.applicationContext = event.getApplicationContext();
        ApplicationContextUtil.domain = domain;
        ApplicationContextUtil.domainWithContext = domainWithContext;
        ApplicationContextUtil.previewPrefix = previewPrefix;
    }
}
