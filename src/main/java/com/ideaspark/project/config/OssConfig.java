package com.ideaspark.project.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssConfig {

    /**
     * 创建 OSS 客户端（仅当 oss.enabled=true 时启用）
     */
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnProperty(prefix = "oss", name = "enabled", havingValue = "true")
    public OSS ossClient(OssProperties ossProperties) {
        return new OSSClientBuilder().build(
                ossProperties.getEndpoint(),
                ossProperties.getAccessKeyId(),
                ossProperties.getAccessKeySecret()
        );
    }
}

