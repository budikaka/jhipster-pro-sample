package com.zbdigit.myapp.config;

import com.zbdigit.myapp.oss.builder.OssBuilder;
import com.zbdigit.myapp.service.OssConfigService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Oss配置类
 *
 */
@Configuration
public class OssConfiguration {

    private final ApplicationProperties applicationProperties;

    private final OssConfigService ossConfigService;

    @Bean
    public OssBuilder ossBuilder() {
        return new OssBuilder(applicationProperties, ossConfigService);
    }

    public OssConfiguration(ApplicationProperties applicationProperties, OssConfigService ossConfigService) {
        this.applicationProperties = applicationProperties;
        this.ossConfigService = ossConfigService;
    }
}
