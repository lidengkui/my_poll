package com.poll.service.conf;

import com.poll.common.util.CheckUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.util.List;
import java.util.Map;


@ConfigurationProperties(prefix = "uploadConf")
@Component
@Data
public class UploadConf {
    private Map<String, Config> configs;

    @Data
    public static class Config{
        private String dir;
        private List<String> allowType;
        private long maxSize;
        private int num;
        private boolean dateDir;

        /**
         * 自校验方法
         * @return
         */
        public boolean check() {
            if (CheckUtil.isNotEmpty(dir) && maxSize > 0 && num > 0) {
                return true;
            }
            return false;
        }
    }

    @Bean(name = "multipartResolver")
    public MultipartResolver multipartResolver(){
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        resolver.setResolveLazily(true);//resolveLazily属性启用是为了推迟文件解析，以在在UploadAction中捕获文件大小异常
        resolver.setMaxInMemorySize(40960);
        resolver.setMaxUploadSize(50*1024*1024);//上传文件大小 50M 50*1024*1024
        return resolver;
    }
}
