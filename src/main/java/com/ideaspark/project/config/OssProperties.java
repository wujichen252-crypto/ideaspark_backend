package com.ideaspark.project.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "oss")
public class OssProperties {

    private boolean enabled = false;

    private String endpoint;

    private String accessKeyId;

    private String accessKeySecret;

    private String bucket;

    private String baseDir = "uploads";
}

