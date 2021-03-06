package com.zbdigit.myapp.oss.builder;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import com.zbdigit.myapp.config.ApplicationProperties;
import com.zbdigit.myapp.domain.OssConfig;
import com.zbdigit.myapp.oss.OssRule;
import com.zbdigit.myapp.oss.OssTemplate;
import com.zbdigit.myapp.oss.tencent.TencentCosTemplate;

/**
 * 腾讯云存储构建类
 *
 */
public class TencentOssBuilder {

    public static OssTemplate template(OssConfig ossConfig, OssRule ossRule) {
        // 创建配置类
        ApplicationProperties applicationProperties = new ApplicationProperties();
        applicationProperties.getOss().setEndpoint(ossConfig.getEndpoint());
        applicationProperties.getOss().setAccessKey(ossConfig.getAccessKey());
        applicationProperties.getOss().setSecretKey(ossConfig.getSecretKey());
        applicationProperties.getOss().setBucketName(ossConfig.getBucketName());
        applicationProperties.getOss().setAppId(ossConfig.getAppId());
        applicationProperties.getOss().setRegion(ossConfig.getRegion());
        // 初始化用户身份信息（secretId, secretKey）
        COSCredentials credentials = new BasicCOSCredentials(
            applicationProperties.getOss().getAccessKey(),
            applicationProperties.getOss().getSecretKey()
        );
        // 设置 bucket 的区域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        Region region = new Region(applicationProperties.getOss().getRegion());
        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        ClientConfig clientConfig = new ClientConfig(region);
        // 设置OSSClient允许打开的最大HTTP连接数，默认为1024个。
        clientConfig.setMaxConnectionsCount(1024);
        // 设置Socket层传输数据的超时时间，默认为50000毫秒。
        clientConfig.setSocketTimeout(50000);
        // 设置建立连接的超时时间，默认为50000毫秒。
        clientConfig.setConnectionTimeout(50000);
        // 设置从连接池中获取连接的超时时间（单位：毫秒），默认不超时。
        clientConfig.setConnectionRequestTimeout(1000);
        COSClient cosClient = new COSClient(credentials, clientConfig);
        return new TencentCosTemplate(cosClient, applicationProperties, ossRule);
    }
}
