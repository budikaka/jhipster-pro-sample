package com.zbdigit.myapp.config;

import com.google.common.collect.Maps;
import java.util.Map;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.config.AuthDefaultSource;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Jhipster Sample Application.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Upload upload = new Upload();
    private final SmsConfig smsConfig = new SmsConfig();
    private Pay pay = new Pay();
    private Face face = new Face();
    private Oss oss = new Oss();
    private Sms sms = new Sms();
    private Social social = new Social();

    public Social getSocial() {
        return social;
    }

    public void setSocial(Social social) {
        this.social = social;
    }

    public Oss getOss() {
        return oss;
    }

    public void setOss(Oss oss) {
        this.oss = oss;
    }

    public Face getFace() {
        return face;
    }

    public Sms getSms() {
        return sms;
    }

    public void setSms(Sms sms) {
        this.sms = sms;
    }

    public void setFace(Face face) {
        this.face = face;
    }

    public Upload getUpload() {
        return upload;
    }

    public SmsConfig getSmsConfig() {
        return smsConfig;
    }

    public Pay getPay() {
        return pay;
    }

    public void setPay(Pay pay) {
        this.pay = pay;
    }

    public static class Upload {

        private String rootPath;
        private String domainUrl;
        private String type;
        private AliOss aliOss;
        private Qcos qcos;
        private QiniuOss qiniuOss;

        public String getRootPath() {
            return rootPath;
        }

        public void setRootPath(String rootPath) {
            this.rootPath = rootPath;
        }

        public String getDomainUrl() {
            return domainUrl;
        }

        public void setDomainUrl(String domainUrl) {
            this.domainUrl = domainUrl;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public AliOss getAliOss() {
            return aliOss;
        }

        public void setAliOss(AliOss aliOss) {
            this.aliOss = aliOss;
        }

        public Qcos getQcos() {
            return qcos;
        }

        public void setQcos(Qcos qcos) {
            this.qcos = qcos;
        }

        public QiniuOss getQiniuOss() {
            return qiniuOss;
        }

        public void setQiniuOss(QiniuOss qiniuOss) {
            this.qiniuOss = qiniuOss;
        }

        public static class AliOss {

            private String endpoint;
            private String accessKey;
            private String secretKey;
            private String bucketName;
            private String staticDomain;

            public String getEndpoint() {
                return endpoint;
            }

            public void setEndpoint(String endpoint) {
                this.endpoint = endpoint;
            }

            public String getAccessKey() {
                return accessKey;
            }

            public void setAccessKey(String accessKey) {
                this.accessKey = accessKey;
            }

            public String getSecretKey() {
                return secretKey;
            }

            public void setSecretKey(String secretKey) {
                this.secretKey = secretKey;
            }

            public String getBucketName() {
                return bucketName;
            }

            public void setBucketName(String bucketName) {
                this.bucketName = bucketName;
            }

            public String getStaticDomain() {
                return staticDomain;
            }

            public void setStaticDomain(String staticDomain) {
                this.staticDomain = staticDomain;
            }
        }

        public static class Qcos {

            private String bucketArea;
            private String appId;
            private String secretId;
            private String appKey;
            private String bucketName;

            public String getBucketArea() {
                return bucketArea;
            }

            public void setBucketArea(String bucketArea) {
                this.bucketArea = bucketArea;
            }

            public String getAppId() {
                return appId;
            }

            public void setAppId(String appId) {
                this.appId = appId;
            }

            public String getSecretId() {
                return secretId;
            }

            public void setSecretId(String secretId) {
                this.secretId = secretId;
            }

            public String getAppKey() {
                return appKey;
            }

            public void setAppKey(String appKey) {
                this.appKey = appKey;
            }

            public String getBucketName() {
                return bucketName;
            }

            public void setBucketName(String bucketName) {
                this.bucketName = bucketName;
            }
        }

        public static class QiniuOss {

            private String buckArea;
            private String bucket;
            private String accessKey;
            private String secretKey;

            public String getBuckArea() {
                return buckArea;
            }

            public void setBuckArea(String buckArea) {
                this.buckArea = buckArea;
            }

            public String getBucket() {
                return bucket;
            }

            public void setBucket(String bucket) {
                this.bucket = bucket;
            }

            public String getAccessKey() {
                return accessKey;
            }

            public void setAccessKey(String accessKey) {
                this.accessKey = accessKey;
            }

            public String getSecretKey() {
                return secretKey;
            }

            public void setSecretKey(String secretKey) {
                this.secretKey = secretKey;
            }
        }
    }

    public static class SmsConfig {

        private DaYuConfig daYuConfig;

        public DaYuConfig getDaYuConfig() {
            return daYuConfig;
        }

        public void setDaYuConfig(DaYuConfig daYuConfig) {
            this.daYuConfig = daYuConfig;
        }

        public static class DaYuConfig {

            private String regionId;
            private String accessKeyId;
            private String secret;

            public String getRegionId() {
                return regionId;
            }

            public void setRegionId(String regionId) {
                this.regionId = regionId;
            }

            public String getAccessKeyId() {
                return accessKeyId;
            }

            public void setAccessKeyId(String accessKeyId) {
                this.accessKeyId = accessKeyId;
            }

            public String getSecret() {
                return secret;
            }

            public void setSecret(String secret) {
                this.secret = secret;
            }
        }
    }

    public static class Pay {

        private WxPay wxPay;

        public WxPay getWxPay() {
            return wxPay;
        }

        public void setWxPay(WxPay wxPay) {
            this.wxPay = wxPay;
        }

        public static class WxPay {

            /**
             * ??????????????????????????????????????????appid
             */
            private String appId;

            /**
             * ?????????????????????
             */
            private String mchId;

            /**
             * ????????????????????????
             */
            private String mchKey;

            /**
             * ??????????????????????????????????????????ID????????????????????????????????????????????????????????????????????????
             */
            private String subAppId;

            /**
             * ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
             */
            private String subMchId;

            /**
             * apiclient_cert.p12????????????????????????????????????????????????????????????classpath:????????????
             */
            private String keyPath;

            public String getAppId() {
                return this.appId;
            }

            public void setAppId(String appId) {
                this.appId = appId;
            }

            public String getMchId() {
                return mchId;
            }

            public void setMchId(String mchId) {
                this.mchId = mchId;
            }

            public String getMchKey() {
                return mchKey;
            }

            public void setMchKey(String mchKey) {
                this.mchKey = mchKey;
            }

            public String getSubAppId() {
                return subAppId;
            }

            public void setSubAppId(String subAppId) {
                this.subAppId = subAppId;
            }

            public String getSubMchId() {
                return subMchId;
            }

            public void setSubMchId(String subMchId) {
                this.subMchId = subMchId;
            }

            public String getKeyPath() {
                return this.keyPath;
            }

            public void setKeyPath(String keyPath) {
                this.keyPath = keyPath;
            }
        }
    }

    public static class Face {

        private ArcFace arcFace;

        public ArcFace getArcFace() {
            return arcFace;
        }

        public void setArcFace(ArcFace arcFace) {
            this.arcFace = arcFace;
        }

        public static class ArcFace {

            private String appId;
            private String sdkKey;

            public String getAppId() {
                return appId;
            }

            public void setAppId(String appId) {
                this.appId = appId;
            }

            public String getSdkKey() {
                return sdkKey;
            }

            public void setSdkKey(String sdkKey) {
                this.sdkKey = sdkKey;
            }
        }
    }

    public static class Oss {

        /**
         * ????????????
         */
        private Boolean enabled;

        /**
         * ??????????????????
         */
        private String name;

        /**
         * ????????????????????????
         */
        private Boolean tenantMode = false;

        /**
         * ?????????????????????URL
         */
        private String endpoint;

        /**
         * ??????ID TencentCOS??????
         */
        private String appId;

        /**
         * ???????????? TencentCOS??????
         */
        private String region;

        /**
         * Access key????????????ID?????????????????????????????????
         */
        private String accessKey;

        /**
         * Secret key?????????????????????
         */
        private String secretKey;

        /**
         * ????????????????????????
         */
        private String bucketName = "bladex";

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getTenantMode() {
            return tenantMode;
        }

        public void setTenantMode(Boolean tenantMode) {
            this.tenantMode = tenantMode;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }
    }

    public static class Sms {

        /**
         * ????????????
         */
        private Boolean enabled;

        /**
         * ??????????????????
         */
        private String name;

        /**
         * ????????????ID
         */
        private String templateId;

        /**
         * regionId
         */
        private String regionId = "cn-hangzhou";

        /**
         * accessKey
         */
        private String accessKey;

        /**
         * secretKey
         */
        private String secretKey;

        /**
         * ????????????
         */
        private String signName;

        public Boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTemplateId() {
            return templateId;
        }

        public void setTemplateId(String templateId) {
            this.templateId = templateId;
        }

        public String getRegionId() {
            return regionId;
        }

        public void setRegionId(String regionId) {
            this.regionId = regionId;
        }

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getSignName() {
            return signName;
        }

        public void setSignName(String signName) {
            this.signName = signName;
        }
    }

    public static class Social {

        /**
         * ??????
         */
        private Boolean enabled = false;

        /**
         * ????????????
         */
        private String domain;

        /**
         * ??????
         */
        private Map<AuthDefaultSource, AuthConfig> oauth = Maps.newHashMap();

        /**
         * ??????
         */
        private Map<String, String> alias = Maps.newHashMap();

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public Map<AuthDefaultSource, AuthConfig> getOauth() {
            return oauth;
        }

        public void setOauth(Map<AuthDefaultSource, AuthConfig> oauth) {
            this.oauth = oauth;
        }

        public Map<String, String> getAlias() {
            return alias;
        }

        public void setAlias(Map<String, String> alias) {
            this.alias = alias;
        }
    }
}
