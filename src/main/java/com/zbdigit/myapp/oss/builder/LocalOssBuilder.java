package com.zbdigit.myapp.oss.builder;

import com.zbdigit.myapp.config.ApplicationProperties;
import com.zbdigit.myapp.domain.OssConfig;
import com.zbdigit.myapp.oss.OssRule;
import com.zbdigit.myapp.oss.OssTemplate;
import com.zbdigit.myapp.oss.local.LocalOssTemplate;

/**
 * 本地存储构建类
 *
 */
public class LocalOssBuilder {

    public static OssTemplate template(OssConfig ossConfig, OssRule ossRule) {
        // 创建配置类
        ApplicationProperties applicationProperties = new ApplicationProperties();
        applicationProperties.getOss().setEndpoint(ossConfig.getEndpoint());
        applicationProperties.getOss().setAccessKey(ossConfig.getAccessKey());
        applicationProperties.getOss().setSecretKey(ossConfig.getSecretKey());
        applicationProperties.getOss().setBucketName(ossConfig.getBucketName());
        return new LocalOssTemplate(applicationProperties, ossRule);
    }
}
