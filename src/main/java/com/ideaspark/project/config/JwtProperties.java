package com.ideaspark.project.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;

    private long expireSeconds = 3600;

    private String issuer = "ideaspark";
}

